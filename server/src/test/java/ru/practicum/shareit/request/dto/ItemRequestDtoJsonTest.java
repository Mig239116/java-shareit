package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void newItemRequestDtoSerializationTest() {
        NewItemRequestDto dto = new NewItemRequestDto("Need a drill");
        String expectedJson = "{\"description\":\"Need a drill\"}";

        String actualJson = objectMapper.writeValueAsString(dto);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    void newItemRequestDtoDeserializationTest() {
        String json = "{\"description\":\"Need a drill\"}";
        NewItemRequestDto dto = objectMapper.readValue(json, NewItemRequestDto.class);

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    @SneakyThrows
    void itemRequestDtoSerializationTest() {
        User user = new User(1L, "John", "john@example.com");
        ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", user, LocalDateTime.now());

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"requestor\":{\"id\":1,\"name\":\"John\"");
    }

    @Test
    @SneakyThrows
    void responseItemRequestDtoSerializationTest() {
        User user = new User(1L, "John", "john@example.com");
        ResponseItemRequestDto dto = new ResponseItemRequestDto(
                1L, "Need a drill", user, LocalDateTime.now(), Collections.emptyList());

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"items\":[]");
    }
}
