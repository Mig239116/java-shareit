package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto() {
        Comment comment = new Comment(1L, "Text",
                new Item(), new User(1L, "Author", "email@test.com"),
                LocalDateTime.now());

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void requestToComment() {
        CommentRequestDto requestDto = new CommentRequestDto("Text");
        Item item = new Item();
        User author = new User(1L, "Author", "email@test.com");

        Comment comment = CommentMapper.requestToComment(requestDto, item, author);

        assertEquals(requestDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertNotNull(comment.getCreated());
    }
}