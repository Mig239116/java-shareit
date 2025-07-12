package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @Qualifier("bookingServiceImpl")
    private BookingService bookingService;

    private NewBookingDto newBookingDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        User booker = new User(1L, "Booker", "booker@email.com");
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null, null, null, null);

        newBookingDto = new NewBookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        bookingDto = new BookingDto(
                1L,
                newBookingDto.getStart(),
                newBookingDto.getEnd(),
                itemDto,
                booker,
                BookingStatus.WAITING
        );
    }

    @Test
    void addBooking_ShouldReturnCreated() throws Exception {
        when(bookingService.addBooking(any(NewBookingDto.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void confirmBooking_ShouldReturnOk() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.confirmBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_ShouldReturnOk() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserBookings_ShouldReturnOk() throws Exception {
        when(bookingService.getUserBookings(any(BookingState.class), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getBookingsByOwner_ShouldReturnOk() throws Exception {
        when(bookingService.getBookingsByOwner(any(BookingState.class), anyLong()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void addBooking_WhenInvalidDates_ShouldReturnBadRequest() throws Exception {
        NewBookingDto invalidBooking = new NewBookingDto(
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        when(bookingService.addBooking(any(NewBookingDto.class), anyLong()))
                .thenThrow(new BadRequestException("The start date is after or equals to end date"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request"))
                .andExpect(jsonPath("$.description").exists());
    }


}