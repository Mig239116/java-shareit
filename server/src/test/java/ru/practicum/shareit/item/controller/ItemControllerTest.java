package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("itemServiceImpl")
    private ItemService itemService;

    private ItemDto itemDto;
    private NewItemDto newItemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Item", "Description", true, null, null, null, null, null);
        newItemDto = new NewItemDto("Item", "Description", true, null);
        commentDto = new CommentDto(1L, "Comment", "Author", LocalDateTime.now());
    }

    @Test
    void addItem_shouldReturnCreatedItem() throws Exception {
        Mockito.when(itemService.addItem(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void editItem_shouldReturnUpdatedItem() throws Exception {
        Mockito.when(itemService.editItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new UpdateItemRequest("Updated", "Desc", true)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        Mockito.when(itemService.getItem(anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void getItems_shouldReturnUserItems() throws Exception {
        Mockito.when(itemService.getItems(anyLong()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        Mockito.when(itemService.searchItems(anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new CommentRequestDto("Comment")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class));
    }
}