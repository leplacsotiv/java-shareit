package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createShouldSaveRequestWithEmptyItems() {
        User requester = saveUser("Ivan", "ivan@test.com");

        ItemRequestDto result = itemRequestService.create(
                requester.getId(),
                new CreateItemRequestDto("Нужна дрель")
        );

        assertThat(result.id()).isNotNull();
        assertThat(result.description()).isEqualTo("Нужна дрель");
        assertThat(result.created()).isNotNull();
        assertThat(result.items()).isEmpty();

        List<ItemRequest> savedRequests = itemRequestRepository.findAll();
        assertThat(savedRequests).hasSize(1);
        assertThat(savedRequests.getFirst().getRequester().getId()).isEqualTo(requester.getId());
    }

    @Test
    void getByRequesterShouldReturnOwnRequestsWithItemsSortedByCreatedDesc() {
        User requester = saveUser("Ivan", "ivan@test.com");
        User owner = saveUser("Petr", "petr@test.com");

        ItemRequest olderRequest = saveRequest(
                requester,
                "Нужна стремянка",
                LocalDateTime.now().minusDays(2)
        );
        ItemRequest newerRequest = saveRequest(
                requester,
                "Нужна дрель",
                LocalDateTime.now().minusDays(1)
        );

        saveItem(owner, newerRequest, "Дрель Bosch");

        List<ItemRequestDto> result = itemRequestService.getByRequester(requester.getId())
                .stream()
                .toList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(newerRequest.getId());
        assertThat(result.get(0).description()).isEqualTo("Нужна дрель");
        assertThat(result.get(0).items()).hasSize(1);
        assertThat(result.get(0).items().getFirst().name()).isEqualTo("Дрель Bosch");
        assertThat(result.get(0).items().getFirst().ownerId()).isEqualTo(owner.getId());

        assertThat(result.get(1).id()).isEqualTo(olderRequest.getId());
        assertThat(result.get(1).items()).isEmpty();
    }

    @Test
    void getAllShouldReturnOnlyOtherUsersRequestsWithOffsetPagination() {
        User requester = saveUser("Ivan", "ivan@test.com");
        User otherUser = saveUser("Petr", "petr@test.com");

        saveRequest(requester, "Мой запрос", LocalDateTime.now().minusHours(1));

        ItemRequest olderOtherRequest = saveRequest(
                otherUser,
                "Чужой старый запрос",
                LocalDateTime.now().minusDays(2)
        );
        ItemRequest newerOtherRequest = saveRequest(
                otherUser,
                "Чужой новый запрос",
                LocalDateTime.now().minusDays(1)
        );

        List<ItemRequestDto> firstPage = itemRequestService.getAll(requester.getId(), 0, 10)
                .stream()
                .toList();

        assertThat(firstPage).extracting(ItemRequestDto::id)
                .containsExactly(newerOtherRequest.getId(), olderOtherRequest.getId());

        List<ItemRequestDto> offsetPage = itemRequestService.getAll(requester.getId(), 1, 1)
                .stream()
                .toList();

        assertThat(offsetPage).hasSize(1);
        assertThat(offsetPage.getFirst().id()).isEqualTo(olderOtherRequest.getId());
    }

    @Test
    void getByIdShouldReturnRequestWithItemsForAnyExistingUser() {
        User requester = saveUser("Ivan", "ivan@test.com");
        User owner = saveUser("Petr", "petr@test.com");

        ItemRequest request = saveRequest(
                requester,
                "Нужна дрель",
                LocalDateTime.now().minusDays(1)
        );
        Item item = saveItem(owner, request, "Дрель Bosch");

        ItemRequestDto result = itemRequestService.getById(owner.getId(), request.getId());

        assertThat(result.id()).isEqualTo(request.getId());
        assertThat(result.description()).isEqualTo("Нужна дрель");
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().id()).isEqualTo(item.getId());
        assertThat(result.items().getFirst().name()).isEqualTo("Дрель Bosch");
        assertThat(result.items().getFirst().ownerId()).isEqualTo(owner.getId());
    }

    @Test
    void getByIdShouldThrowNotFoundWhenRequestDoesNotExist() {
        User user = saveUser("Ivan", "ivan@test.com");

        assertThatThrownBy(() -> itemRequestService.getById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item request with id 999 not found");
    }

    private User saveUser(String name, String email) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .build());
    }

    private ItemRequest saveRequest(User requester, String description, LocalDateTime created) {
        return itemRequestRepository.save(ItemRequest.builder()
                .description(description)
                .requester(requester)
                .created(created)
                .build());
    }

    private Item saveItem(User owner, ItemRequest request, String name) {
        return itemRepository.save(Item.builder()
                .name(name)
                .description("Описание")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
    }
}
