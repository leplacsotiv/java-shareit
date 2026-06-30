package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = {"requester"})
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    @EntityGraph(attributePaths = {"requester"})
    List<ItemRequest> findByRequesterIdNot(Long requesterId, Pageable pageable);
}
