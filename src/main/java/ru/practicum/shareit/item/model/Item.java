package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Entity
@Table(name="items")
@Builder
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@ToString
@FieldDefaults(level=AccessLevel.PRIVATE)
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Booking> bookings;

    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;
}
