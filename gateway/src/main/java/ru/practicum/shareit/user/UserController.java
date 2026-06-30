package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateUserDto dto) {
        log.info("Creating user {}", dto);
        return userClient.create(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @Valid @RequestBody UpdateUserDto dto) {
        validateUpdateDto(dto);
        log.info("Updating user {}, dto={}", userId, dto);
        return userClient.update(userId, dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("Get user {}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable Long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteById(userId);
    }

    private void validateUpdateDto(UpdateUserDto dto) {
        if (dto.name() != null && dto.name().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Name must not be blank");
        }

        if (dto.email() != null && dto.email().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email must not be blank");
        }
    }
}
