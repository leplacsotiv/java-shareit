package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ConflictException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(CreateUserDto dto) {
        checkEmailIsFree(dto.email(), null);

        User user = UserMapper.toModel(dto);
        User savedUser = userStorage.save(user);

        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto update(Long userId, UpdateUserDto dto) {
        User user = getUserOrThrow(userId);

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.email() != null) {
            checkEmailIsFree(dto.email(), userId);
            user.setEmail(dto.email());
        }

        User updatedUser = userStorage.update(user);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long userId) {
        userStorage.deleteById(userId);
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private void checkEmailIsFree(String email, Long currentUserId) {
        userStorage.findByEmail(email)
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new ConflictException("Email already exists: " + email);
                });
    }
}