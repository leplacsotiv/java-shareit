package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ErrorHandler.class)
class UserControllerTest {
    private static final String VALID_CREATE_JSON = "{\"name\":\"Ivan\",\"email\":\"ivan@test.com\"}";
    private static final String BLANK_NAME_JSON = "{\"name\":\"\",\"email\":\"ivan@test.com\"}";
    private static final String INVALID_EMAIL_JSON = "{\"name\":\"Ivan\",\"email\":\"not-email\"}";
    private static final String VALID_UPDATE_JSON = "{\"name\":\"Petr\"}";
    private static final String BLANK_UPDATE_NAME_JSON = "{\"name\":\"\"}";
    private static final String BLANK_UPDATE_EMAIL_JSON = "{\"email\":\"\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void createShouldCallClientWhenRequestIsValid() throws Exception {
        when(userClient.create(any(CreateUserDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Ivan",
                        "email", "ivan@test.com"
                )));

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(VALID_CREATE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Ivan")))
                .andExpect(jsonPath("$.email", is("ivan@test.com")));

        ArgumentCaptor<CreateUserDto> captor = ArgumentCaptor.forClass(CreateUserDto.class);

        verify(userClient).create(captor.capture());

        assertThat(captor.getValue().name()).isEqualTo("Ivan");
        assertThat(captor.getValue().email()).isEqualTo("ivan@test.com");
    }

    @Test
    void createShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(BLANK_NAME_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Name must not be blank")));
    }

    @Test
    void createShouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(INVALID_EMAIL_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Email must be valid")));
    }

    @Test
    void updateShouldCallClientWhenRequestIsValid() throws Exception {
        when(userClient.update(eq(1L), any(UpdateUserDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Petr",
                        "email", "ivan@test.com"
                )));

        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(VALID_UPDATE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Petr")));

        verify(userClient).update(eq(1L), any(UpdateUserDto.class));
    }

    @Test
    void updateShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(BLANK_UPDATE_NAME_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Name must not be blank")));
    }

    @Test
    void updateShouldReturnBadRequestWhenEmailIsBlank() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(BLANK_UPDATE_EMAIL_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Email must not be blank")));
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        when(userClient.getById(1L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Ivan",
                        "email", "ivan@test.com"
                )));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(userClient).getById(1L);
    }

    @Test
    void getAllShouldCallClient() throws Exception {
        when(userClient.getAll())
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAll();
    }

    @Test
    void deleteShouldCallClient() throws Exception {
        when(userClient.deleteById(1L))
                .thenReturn(ResponseEntity.ok(null));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).deleteById(1L);
    }
}
