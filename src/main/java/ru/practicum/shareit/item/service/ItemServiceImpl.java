package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long userId, CreateItemDto dto) {
        User owner = getUserOrThrow(userId);

        Item item = ItemMapper.toModel(dto, owner);
        Item savedItem = itemStorage.save(item);

        return ItemMapper.toDto(savedItem);
    }

    @Override
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

        Item updatedItem = itemStorage.update(item);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        getUserOrThrow(userId);

        Item item = getItemOrThrow(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public Collection<ItemDto> getByOwnerId(Long userId) {
        getUserOrThrow(userId);

        return itemStorage.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        getUserOrThrow(userId);

        return itemStorage.searchAvailableByText(text)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }

    private void checkOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update item");
        }
    }
}