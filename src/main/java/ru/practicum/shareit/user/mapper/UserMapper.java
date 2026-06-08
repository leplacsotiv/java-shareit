package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        Objects.requireNonNull(user, "user must not be null");

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toModel(CreateUserDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }
}