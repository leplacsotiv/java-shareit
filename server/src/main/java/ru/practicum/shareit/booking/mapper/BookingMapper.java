package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@UtilityClass
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        Objects.requireNonNull(booking, "booking must not be null");

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toDto(booking.getItem()),
                UserMapper.toDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public BookingShortDto toShortDto(Booking booking) {
        Objects.requireNonNull(booking, "booking must not be null");

        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public Booking toModel(CreateBookingDto dto, Item item, User booker) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(item, "item must not be null");
        Objects.requireNonNull(booker, "booker must not be null");

        return Booking.builder()
                .start(dto.start())
                .end(dto.end())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}