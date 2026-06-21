package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment comment) {
        Objects.requireNonNull(comment, "comment must not be null");

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toModel(AddCommentDto dto, Item item, User author) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(item, "item must not be null");
        Objects.requireNonNull(author, "author must not be null");

        return Comment.builder()
                .text(dto.text())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }
}