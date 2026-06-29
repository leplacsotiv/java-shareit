package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(Long userId, CreateBookingDto dto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    Collection<BookingDto> getByBooker(Long userId, BookingState state);

    Collection<BookingDto> getByOwner(Long userId, BookingState state);
}