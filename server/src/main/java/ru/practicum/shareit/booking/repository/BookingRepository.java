package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAll();

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemIdInAndStatusAndStartBeforeOrderByStartDesc(
            Collection<Long> itemIds,
            BookingStatus status,
            LocalDateTime start
    );

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemIdInAndStatusAndStartAfterOrderByStartAsc(
            Collection<Long> itemIds,
            BookingStatus status,
            LocalDateTime start
    );

    boolean existsByItemIdAndBookerIdAndEndBefore(
            Long itemId,
            Long bookerId,
            LocalDateTime end
    );
}