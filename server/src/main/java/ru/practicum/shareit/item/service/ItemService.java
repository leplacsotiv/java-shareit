package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, CreateItemDto dto);

    ItemDto update(Long userId, Long itemId, UpdateItemDto dto);

    ItemDto getById(Long userId, Long itemId);

    Collection<ItemDto> getByOwnerId(Long userId, Integer from, Integer size);

    Collection<ItemDto> search(Long userId, String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, AddCommentDto dto);
}
