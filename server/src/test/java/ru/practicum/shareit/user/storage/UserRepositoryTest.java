package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(null, "User1", "user1@example.com");
        user2 = new User(null, "User2", "user2@example.com");
        em.persist(user1);
        em.persist(user2);
        em.flush();
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("User1", foundUser.get().getName());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<User> foundUser = userRepository.findById(999L);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void existsByEmail_shouldReturnTrueForExistingEmail() {
        boolean exists = userRepository.existsByEmail("user1@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_shouldReturnFalseForNonExistingEmail() {
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");

        assertFalse(exists);
    }

    @Test
    void save_shouldPersistUser() {
        User newUser = new User(null, "NewUser", "new@example.com");
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("NewUser", savedUser.getName());
        assertEquals("new@example.com", savedUser.getEmail());

        User foundUser = em.find(User.class, savedUser.getId());
        assertEquals(savedUser, foundUser);
    }

    @Test
    void deleteById_shouldRemoveUser() {
        userRepository.deleteById(user1.getId());

        User deletedUser = em.find(User.class, user1.getId());
        assertNull(deletedUser);
    }
}