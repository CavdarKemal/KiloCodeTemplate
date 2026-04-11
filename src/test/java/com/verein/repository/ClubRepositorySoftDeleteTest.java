package com.verein.repository;

import com.verein.entity.Club;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClubRepositorySoftDeleteTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    void setUp() {
        Club club1 = Club.builder()
                .name("FC Berlin")
                .city("Berlin")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(club1);

        Club club2 = Club.builder()
                .name("FC Hamburg")
                .city("Hamburg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .deletedBy("admin")
                .build();
        entityManager.persist(club2);

        entityManager.flush();
    }

    @Test
    void testFindAllActiveReturnsOnlyNonDeleted() {
        List<Club> result = clubRepository.findAllActive();
        
        assertEquals(1, result.size());
        assertEquals("FC Berlin", result.get(0).getName());
    }

    @Test
    void testFindAllDeletedReturnsOnlyDeleted() {
        List<Club> result = clubRepository.findAllDeleted();
        
        assertEquals(1, result.size());
        assertEquals("FC Hamburg", result.get(0).getName());
    }

    @Test
    void testFindByIdActiveReturnsOnlyNonDeleted() {
        Club active = clubRepository.findAllActive().get(0);
        Optional<Club> result = clubRepository.findByIdActive(active.getId());
        
        assertTrue(result.isPresent());
        assertEquals("FC Berlin", result.get().getName());
    }

    @Test
    void testFindByIdActiveReturnsEmptyForDeleted() {
        List<Club> deleted = clubRepository.findAllDeleted();
        Optional<Club> result = clubRepository.findByIdActive(deleted.get(0).getId());
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testSoftDelete() {
        Club club = clubRepository.findAllActive().get(0);
        clubRepository.softDeleteById(club.getId(), "testuser");
        
        Optional<Club> result = clubRepository.findByIdActive(club.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void testRestoreById() {
        Club deleted = clubRepository.findAllDeleted().get(0);
        clubRepository.restoreById(deleted.getId());
        
        Optional<Club> result = clubRepository.findByIdActive(deleted.getId());
        assertTrue(result.isPresent());
    }
}