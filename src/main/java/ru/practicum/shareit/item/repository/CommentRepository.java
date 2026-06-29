package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "item"})
    List<Comment> findByItemIdOrderByCreatedAsc(Long itemId);

    @EntityGraph(attributePaths = {"author", "item"})
    List<Comment> findByItemIdInOrderByCreatedAsc(Collection<Long> itemIds);
}