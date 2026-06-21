package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Collection<User> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}