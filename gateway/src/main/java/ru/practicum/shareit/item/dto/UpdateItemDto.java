package ru.practicum.shareit.item.dto;

public record UpdateItemDto(
        String name,
        String description,
        Boolean available
) {
}
