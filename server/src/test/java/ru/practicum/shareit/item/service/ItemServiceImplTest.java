package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createShouldSaveItemWithRequestId() {
        User requester = saveUser("Ivan", "ivan-item-requester@test.com");
        User owner = saveUser("Petr", "petr-item-owner@test.com");
        ItemRequest request = saveRequest(requester, "Нужна дрель");

        CreateItemDto createItemDto = new CreateItemDto(
                "Дрель Bosch",
                "Мощная дрель",
                true,
                request.getId()
        );

        ItemDto result = itemService.create(owner.getId(), createItemDto);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Дрель Bosch");
        assertThat(result.description()).isEqualTo("Мощная дрель");
        assertThat(result.available()).isTrue();
        assertThat(result.requestId()).isEqualTo(request.getId());

        Item savedItem = itemRepository.findById(result.id()).orElseThrow();

        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(savedItem.getRequest()).isNotNull();
        assertThat(savedItem.getRequest().getId()).isEqualTo(request.getId());
    }

    @Test
    void createShouldSaveItemWithoutRequestId() {
        User owner = saveUser("Petr", "petr-no-request@test.com");

        CreateItemDto createItemDto = new CreateItemDto(
                "Молоток",
                "Обычный молоток",
                true,
                null
        );

        ItemDto result = itemService.create(owner.getId(), createItemDto);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Молоток");
        assertThat(result.requestId()).isNull();

        Item savedItem = itemRepository.findById(result.id()).orElseThrow();

        assertThat(savedItem.getRequest()).isNull();
    }

    @Test
    void createShouldThrowNotFoundWhenOwnerDoesNotExist() {
        CreateItemDto createItemDto = new CreateItemDto(
                "Дрель Bosch",
                "Мощная дрель",
                true,
                null
        );

        assertThatThrownBy(() -> itemService.create(999L, createItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id 999 not found");
    }

    @Test
    void createShouldThrowNotFoundWhenRequestDoesNotExist() {
        User owner = saveUser("Petr", "petr-missing-request@test.com");

        CreateItemDto createItemDto = new CreateItemDto(
                "Дрель Bosch",
                "Мощная дрель",
                true,
                999L
        );

        assertThatThrownBy(() -> itemService.create(owner.getId(), createItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item request with id 999 not found");
    }

    private User saveUser(String name, String email) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .build());
    }

    private ItemRequest saveRequest(User requester, String description) {
        return itemRequestRepository.save(ItemRequest.builder()
                .description(description)
                .requester(requester)
                .created(LocalDateTime.now())
                .build());
    }
}
