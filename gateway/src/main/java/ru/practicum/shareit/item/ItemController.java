package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody CreateItemDto dto) {
        log.info("Creating item {}, userId={}", dto, userId);
        return itemClient.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody UpdateItemDto dto) {
        validateUpdateDto(dto);
        log.info("Updating item {}, userId={}, dto={}", itemId, userId, dto);
        return itemClient.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(USER_ID_HEADER) long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items by owner, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items, userId={}, text={}, from={}, size={}", userId, text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody AddCommentDto dto) {
        log.info("Add comment to item {}, userId={}, dto={}", itemId, userId, dto);
        return itemClient.addComment(userId, itemId, dto);
    }

    private void validateUpdateDto(UpdateItemDto dto) {
        if (dto.name() != null && dto.name().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Item name must not be blank");
        }

        if (dto.description() != null && dto.description().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Item description must not be blank");
        }
    }
}
