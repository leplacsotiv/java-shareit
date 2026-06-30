package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.pagination.OffsetPageRequest;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, CreateItemDto dto) {
        User owner = getUserOrThrow(userId);
        ItemRequest request = getItemRequestOrNull(dto.requestId());

        Item item = ItemMapper.toModel(dto, owner, request);
        Item savedItem = itemRepository.save(item);

        return ItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, UpdateItemDto dto) {
        getUserOrThrow(userId);

        Item item = getItemOrThrow(itemId);
        checkOwner(item, userId);

        if (dto.name() != null) {
            item.setName(dto.name());
        }

        if (dto.description() != null) {
            item.setDescription(dto.description());
        }

        if (dto.available() != null) {
            item.setAvailable(dto.available());
        }

        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        getUserOrThrow(userId);

        Item item = getItemOrThrow(itemId);
        List<CommentDto> comments = findCommentsByItemIds(List.of(itemId))
                .getOrDefault(itemId, List.of());

        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toDto(item, null, null, comments);
        }

        Map<Long, BookingShortDto> lastBookings = findLastBookingsByItemIds(List.of(itemId));
        Map<Long, BookingShortDto> nextBookings = findNextBookingsByItemIds(List.of(itemId));

        return ItemMapper.toDto(
                item,
                lastBookings.get(itemId),
                nextBookings.get(itemId),
                comments
        );
    }

    @Override
    public Collection<ItemDto> getByOwnerId(Long userId, Integer from, Integer size) {
        getUserOrThrow(userId);

        Pageable pageRequest = new OffsetPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findByOwnerId(userId, pageRequest);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, BookingShortDto> lastBookings = findLastBookingsByItemIds(itemIds);
        Map<Long, BookingShortDto> nextBookings = findNextBookingsByItemIds(itemIds);
        Map<Long, List<CommentDto>> commentsByItemId = findCommentsByItemIds(itemIds);

        return items.stream()
                .map(item -> ItemMapper.toDto(
                        item,
                        lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()),
                        commentsByItemId.getOrDefault(item.getId(), List.of())
                ))
                .toList();
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text, Integer from, Integer size) {
        getUserOrThrow(userId);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        Pageable pageRequest = new OffsetPageRequest(from, size);

        return itemRepository.searchAvailableByText(text, pageRequest)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }

    private ItemRequest getItemRequestOrNull(Long requestId) {
        if (requestId == null) {
            return null;
        }

        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id " + requestId + " not found"));
    }

    private void checkOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update item");
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, AddCommentDto dto) {
        User author = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

        boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId,
                userId,
                LocalDateTime.now()
        );

        if (!hasCompletedBooking) {
            throw new ValidationException("User with id " + userId
                    + " cannot comment item with id " + itemId);
        }

        Comment comment = Comment.builder()
                .text(dto.text())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }


    private Map<Long, BookingShortDto> findLastBookingsByItemIds(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByItemIdInAndStatusAndStartBeforeOrderByStartDesc(
                        itemIds,
                        BookingStatus.APPROVED,
                        now
                );

        Map<Long, BookingShortDto> result = new HashMap<>();

        for (Booking booking : bookings) {
            Long itemId = booking.getItem().getId();
            result.putIfAbsent(itemId, BookingMapper.toShortDto(booking));
        }

        return result;
    }

    private Map<Long, BookingShortDto> findNextBookingsByItemIds(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByItemIdInAndStatusAndStartAfterOrderByStartAsc(
                        itemIds,
                        BookingStatus.APPROVED,
                        now
                );

        Map<Long, BookingShortDto> result = new HashMap<>();

        for (Booking booking : bookings) {
            Long itemId = booking.getItem().getId();
            result.putIfAbsent(itemId, BookingMapper.toShortDto(booking));
        }

        return result;
    }

    private Map<Long, List<CommentDto>> findCommentsByItemIds(List<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return Map.of();
        }

        List<Comment> comments = commentRepository.findByItemIdInOrderByCreatedAsc(itemIds);
        Map<Long, List<CommentDto>> result = new HashMap<>();

        for (Comment comment : comments) {
            Long itemId = comment.getItem().getId();

            result.computeIfAbsent(itemId, id -> new ArrayList<>())
                    .add(CommentMapper.toDto(comment));
        }

        return result;
    }
}