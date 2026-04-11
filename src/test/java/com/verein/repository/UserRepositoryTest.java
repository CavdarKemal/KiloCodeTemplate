package com.verein.repository;

import com.verein.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("USER");
        user.setEnabled(true);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void testFindByUsername() {
        Optional<User> result = userRepository.findByUsername("testuser");
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> result = userRepository.findByUsername("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    void testExistsByUsernameNotFound() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testSaveUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword(passwordEncoder.encode("password"));
        newUser.setRole("ADMIN");
        newUser.setEnabled(true);
        
        User saved = userRepository.save(newUser);
        
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
    }
}