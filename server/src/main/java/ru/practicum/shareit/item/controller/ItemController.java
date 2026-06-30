package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @Valid @RequestBody CreateItemDto dto) {
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @Valid @RequestBody UpdateItemDto dto) {
        validateUpdateDto(dto);
        return itemService.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                           @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getByOwnerId(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestParam String text,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody AddCommentDto dto) {
        return itemService.addComment(userId, itemId, dto);
    }

    private void validateUpdateDto(UpdateItemDto dto) {
        if (dto.name() != null && dto.name().isBlank()) {
            throw new ValidationException("Item name must not be blank");
        }

        if (dto.description() != null && dto.description().isBlank()) {
            throw new ValidationException("Item description must not be blank");
        }
    }
}