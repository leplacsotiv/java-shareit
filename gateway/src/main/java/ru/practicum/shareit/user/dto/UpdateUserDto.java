package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserDto(
        String name,

        @Email(message = "Email must be valid")
        String email
) {
}
