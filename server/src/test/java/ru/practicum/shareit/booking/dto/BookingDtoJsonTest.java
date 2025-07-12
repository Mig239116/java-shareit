package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeBookingDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null, null, null, null);
        User user = new User(1L, "User", "user@email.com");
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 10, 0);

        BookingDto bookingDto = new BookingDto(1L, start, end, itemDto, user, BookingStatus.WAITING);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).containsPattern("\"start\":\"2023-01-01T10:00:00\"");
        assertThat(json).containsPattern("\"end\":\"2023-01-02T10:00:00\"");
    }

    @Test
    void testDeserializeNewBookingDto() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2023-01-01T10:00:00\",\"end\":\"2023-01-02T10:00:00\"}";

        NewBookingDto newBookingDto = objectMapper.readValue(json, NewBookingDto.class);

        assertThat(newBookingDto.getItemId()).isEqualTo(1L);
        assertThat(newBookingDto.getStart()).isEqualTo(LocalDateTime.of(2023, 1, 1, 10, 0));
        assertThat(newBookingDto.getEnd()).isEqualTo(LocalDateTime.of(2023, 1, 2, 10, 0));
    }
}