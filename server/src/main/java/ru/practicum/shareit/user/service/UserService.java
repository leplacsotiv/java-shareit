package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(CreateUserDto dto);

    UserDto update(Long userId, UpdateUserDto dto);

    UserDto getById(Long userId);

    Collection<UserDto> getAll();

    void deleteById(Long userId);
}