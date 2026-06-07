package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toModel(CreateUserDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }
}