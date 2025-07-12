package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("itemRequestServiceImpl")
    private ItemRequestService itemRequestService;

    @Test
    void addItemRequest_shouldReturnCreatedRequest() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto("Need a drill");
        ItemRequestDto responseDto = new ItemRequestDto(1L, "Need a drill",
                new User(1L, "John", "john@example.com"), LocalDateTime.now());

        when(itemRequestService.addItemRequest(any(NewItemRequestDto.class), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isCreated()) // Изменено с isOk() на isCreated()
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a drill")));
    }

    @Test
    void getItemRequests_shouldReturnUserRequests() throws Exception {
        ResponseItemRequestDto responseDto = new ResponseItemRequestDto(
                1L, "Need a drill",
                new User(1L, "John", "john@example.com"),
                LocalDateTime.now(),
                Collections.emptyList());

        when(itemRequestService.getItemRequests(anyLong()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a drill")));
    }

    @Test
    void getAllItemRequests_shouldReturnOtherUsersRequests() throws Exception {
        ResponseItemRequestDto responseDto = new ResponseItemRequestDto(
                1L, "Need a drill",
                new User(2L, "Alice", "alice@example.com"),
                LocalDateTime.now(),
                Collections.emptyList());

        when(itemRequestService.getAllItemRequests(anyLong()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a drill")));
    }

    @Test
    void getItemRequest_shouldReturnRequestById() throws Exception {
        ResponseItemRequestDto responseDto = new ResponseItemRequestDto(
                1L, "Need a drill",
                new User(1L, "John", "john@example.com"),
                LocalDateTime.now(),
                Collections.emptyList());

        when(itemRequestService.getItemRequest(anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a drill")));
    }
}
