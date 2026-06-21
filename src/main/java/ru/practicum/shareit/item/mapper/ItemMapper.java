package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        return toDto(item, null, null, List.of());
    }

    public static ItemDto toDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        return toDto(item, lastBooking, nextBooking, List.of());
    }

    public static ItemDto toDto(Item item,
                                BookingShortDto lastBooking,
                                BookingShortDto nextBooking,
                                List<CommentDto> comments) {
        Objects.requireNonNull(item, "item must not be null");
        Objects.requireNonNull(comments, "comments must not be null");

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static Item toModel(CreateItemDto dto, User owner) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(owner, "owner must not be null");

        return Item.builder()
                .name(dto.name())
                .description(dto.description())
                .available(dto.available())
                .owner(owner)
                .build();
    }
}