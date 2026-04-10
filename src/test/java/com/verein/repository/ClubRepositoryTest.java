package com.verein.repository;

import com.verein.entity.Club;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.yml")
class ClubRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ClubRepository clubRepository;

    @Test
    void findByName_Success() {
        Club club = Club.builder()
                .name("Test Club")
                .description("Test Description")
                .city("Berlin")
                .build();
        entityManager.persist(club);
        entityManager.flush();

        Club found = clubRepository.findByName("Test Club").orElse(null);

        assertNotNull(found);
        assertEquals("Test Club", found.getName());
        assertEquals("Berlin", found.getCity());
    }

    @Test
    void findByName_NotFound() {
        Club found = clubRepository.findByName("Non Existent").orElse(null);

        assertNull(found);
    }

    @Test
    void existsByName_Success() {
        Club club = Club.builder()
                .name("Existing Club")
                .build();
        entityManager.persist(club);
        entityManager.flush();

        boolean exists = clubRepository.existsByName("Existing Club");

        assertTrue(exists);
    }

    @Test
    void existsByName_False() {
        boolean exists = clubRepository.existsByName("Non Existent Club");

        assertFalse(exists);
    }

    @Test
    void saveAndFind_Success() {
        Club club = Club.builder()
                .name("New Club")
                .description("Description")
                .city("Munich")
                .build();
        Club saved = clubRepository.save(club);

        assertNotNull(saved.getId());

        Club found = clubRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("New Club", found.getName());
    }

    @Test
    void delete_Success() {
        Club club = Club.builder()
                .name("To Delete")
                .build();
        Club saved = clubRepository.save(club);
        Long id = saved.getId();

        clubRepository.deleteById(id);

        assertFalse(clubRepository.existsById(id));
    }
}