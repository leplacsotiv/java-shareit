package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateItemRequestDto(
        @NotBlank(message = "Request description must not be blank")
        String description
) {
}
