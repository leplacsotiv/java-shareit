package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, CreateItemRequestDto dto);

    Collection<ItemRequestDto> getByRequester(Long userId);

    Collection<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
