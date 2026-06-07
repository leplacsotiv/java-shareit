package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toModel(CreateItemDto dto, User owner) {
        if (dto == null) {
            return null;
        }

        return Item.builder()
                .name(dto.name())
                .description(dto.description())
                .available(dto.available())
                .owner(owner)
                .build();
    }
}