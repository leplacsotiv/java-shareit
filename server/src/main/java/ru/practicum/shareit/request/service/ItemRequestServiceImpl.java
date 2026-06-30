package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.common.pagination.OffsetPageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, CreateItemRequestDto dto) {
        User requester = getUserOrThrow(userId);

        ItemRequest request = ItemRequestMapper.toModel(dto, requester);
        ItemRequest savedRequest = itemRequestRepository.save(request);

        return ItemRequestMapper.toDto(savedRequest, List.of());
    }

    @Override
    public Collection<ItemRequestDto> getByRequester(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return toDtosWithItems(requests);
    }

    @Override
    public Collection<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        getUserOrThrow(userId);

        Pageable pageRequest = new OffsetPageRequest(
                from,
                size,
                Sort.by(Sort.Direction.DESC, "created")
        );

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId, pageRequest);
        return toDtosWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUserOrThrow(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id " + requestId + " not found"));

        List<Item> items = itemRepository.findByRequestIdInOrderByIdAsc(List.of(requestId));

        return ItemRequestMapper.toDto(request, items);
    }

    private List<ItemRequestDto> toDtosWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<Item>> itemsByRequestId = findItemsByRequestIds(requestIds);

        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<Item>> findItemsByRequestIds(List<Long> requestIds) {
        List<Item> items = itemRepository.findByRequestIdInOrderByIdAsc(requestIds);
        Map<Long, List<Item>> result = new HashMap<>();

        for (Item item : items) {
            Long requestId = item.getRequest().getId();

            result.computeIfAbsent(requestId, id -> new java.util.ArrayList<>())
                    .add(item);
        }

        return result;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }
}
