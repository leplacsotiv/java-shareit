package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return toDto(item, null, null, List.of());
    }

    public ItemDto toDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        return toDto(item, lastBooking, nextBooking, List.of());
    }

    public ItemDto toDto(Item item,
                         BookingShortDto lastBooking,
                         BookingShortDto nextBooking,
                         List<CommentDto> comments) {
        Objects.requireNonNull(item, "item must not be null");
        Objects.requireNonNull(comments, "comments must not be null");

        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId,
                lastBooking,
                nextBooking,
                comments
        );
    }

    public Item toModel(CreateItemDto dto, User owner) {
        return toModel(dto, owner, null);
    }

    public Item toModel(CreateItemDto dto, User owner, ItemRequest request) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(owner, "owner must not be null");

        return Item.builder()
                .name(dto.name())
                .description(dto.description())
                .available(dto.available())
                .owner(owner)
                .request(request)
                .build();
    }
}
