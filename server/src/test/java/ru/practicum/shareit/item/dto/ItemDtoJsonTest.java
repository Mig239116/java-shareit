package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true,
                new User(1L, "Owner", "owner@email.com"),
                null, null, LocalDate.now(), LocalDate.now().plusDays(1));

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item\",\"description\":\"Description\"," +
                "\"available\":true,\"owner\":{\"id\":1,\"name\":\"Owner\",\"email\":\"owner@email.com\"}}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwner().getName()).isEqualTo("Owner");
    }
}