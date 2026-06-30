package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toModel(CreateItemRequestDto dto, User requester) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(requester, "requester must not be null");

        return ItemRequest.builder()
                .description(dto.description())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(items, "items must not be null");

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items.stream()
                        .map(ItemRequestMapper::toItemDto)
                        .toList()
        );
    }

    private ItemRequestItemDto toItemDto(Item item) {
        return new ItemRequestItemDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
