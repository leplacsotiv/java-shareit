package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.common.exception.ErrorHandler;

import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(ErrorHandler.class)
class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createShouldCallClientWhenRequestIsValid() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(bookingClient.create(eq(1L), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "itemId", 10,
                        "status", "WAITING"
                )));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(bookingJson(10L, start, end)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));

        ArgumentCaptor<BookItemRequestDto> captor = ArgumentCaptor.forClass(BookItemRequestDto.class);

        verify(bookingClient).create(eq(1L), captor.capture());

        assertThat(captor.getValue().getItemId()).isEqualTo(10L);
        assertThat(captor.getValue().getStart()).isEqualTo(start);
        assertThat(captor.getValue().getEnd()).isEqualTo(end);
    }

    @Test
    void createShouldReturnBadRequestWhenEndIsBeforeStart() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(bookingJson(10L, start, end)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturnBadRequestWhenItemIdIsMissing() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        String json = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Item id must not be null")));
    }

    @Test
    void approveShouldCallClient() throws Exception {
        when(bookingClient.approve(2L, 5L, true))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 5,
                        "status", "APPROVED"
                )));

        mockMvc.perform(patch("/bookings/5")
                        .header(USER_ID_HEADER, 2L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingClient).approve(2L, 5L, true);
    }

    @Test
    void getByIdShouldCallClient() throws Exception {
        when(bookingClient.getById(1L, 5L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 5,
                        "status", "WAITING"
                )));

        mockMvc.perform(get("/bookings/5")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));

        verify(bookingClient).getById(1L, 5L);
    }

    @Test
    void getByBookerShouldCallClientWithDefaultParams() throws Exception {
        when(bookingClient.getByBooker(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getByBooker(1L, BookingState.ALL, 0, 10);
    }

    @Test
    void getByBookerShouldCallClientWithStateAndPagination() throws Exception {
        when(bookingClient.getByBooker(1L, BookingState.CURRENT, 1, 2))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "CURRENT")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk());

        verify(bookingClient).getByBooker(1L, BookingState.CURRENT, 1, 2);
    }

    @Test
    void getByBookerShouldReturnBadRequestWhenStateIsUnknown() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByBookerShouldReturnBadRequestWhenFromIsNegative() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByBookerShouldReturnBadRequestWhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByOwnerShouldCallClient() throws Exception {
        when(bookingClient.getByOwner(2L, BookingState.PAST, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 2L)
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient).getByOwner(2L, BookingState.PAST, 0, 10);
    }

    private static String bookingJson(Long itemId, LocalDateTime start, LocalDateTime end) {
        return "{\"itemId\":" + itemId
                + ",\"start\":\"" + start
                + "\",\"end\":\"" + end
                + "\"}";
    }
}
