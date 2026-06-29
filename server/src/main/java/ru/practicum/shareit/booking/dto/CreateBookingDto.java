package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateBookingDto(
        @NotNull(message = "Item id must not be null")
        Long itemId,

        @NotNull(message = "Start date must not be null")
        @Future(message = "Start date must be in the future")
        LocalDateTime start,

        @NotNull(message = "End date must not be null")
        @Future(message = "End date must be in the future")
        LocalDateTime end
) {
}