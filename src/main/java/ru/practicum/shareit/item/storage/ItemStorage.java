package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    Collection<Item> findByOwnerId(Long ownerId);

    Collection<Item> searchAvailableByText(String text);
}