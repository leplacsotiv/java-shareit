package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.exception.ErrorHandler;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String VALID_REQUEST_JSON = "{\"description\":\"Нужна дрель\"}";
    private static final String BLANK_DESCRIPTION_JSON = "{\"description\":\"\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createShouldCallClientWhenRequestIsValid() throws Exception {
        when(itemRequestClient.create(eq(1L), any(CreateItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "description", "Нужна дрель",
                        "items", List.of()
                )));

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(VALID_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна дрель")));

        ArgumentCaptor<CreateItemRequestDto> captor = ArgumentCaptor.forClass(CreateItemRequestDto.class);

        verify(itemRequestClient).create(eq(1L), captor.capture());

        assertThat(captor.getValue().description())
                .isEqualTo("Нужна дрель");
    }

    @Test
    void createShouldReturnBadRequestWhenDescriptionIsBlank() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(BLANK_DESCRIPTION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Request description must not be blank")));
    }

    @Test
    void createShouldReturnBadRequestWhenHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(APPLICATION_JSON)
                        .content(VALID_REQUEST_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Required header is missing: X-Sharer-User-Id")));
    }

    @Test
    void getAllShouldCallClientWithPagination() throws Exception {
        when(itemRequestClient.getAll(2L, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 2L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAll(2L, 0, 10);
    }

    @Test
    void getAllShouldReturnBadRequestWhenFromIsNegative() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 2L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllShouldReturnBadRequestWhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 2L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByRequesterShouldCallClient() throws Exception {
        when(itemRequestClient.getByRequester(1L))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemRequestClient).getByRequester(1L);
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        when(itemRequestClient.getById(1L, 5L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 5,
                        "description", "Нужна дрель",
                        "items", List.of()
                )));

        mockMvc.perform(get("/requests/5")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));

        verify(itemRequestClient).getById(1L, 5L);
    }
}
