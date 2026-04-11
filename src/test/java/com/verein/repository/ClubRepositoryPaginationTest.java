package com.verein.repository;

import com.verein.entity.Club;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClubRepositoryPaginationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 15; i++) {
            Club club = Club.builder()
                    .name("Club " + i)
                    .description("Description " + i)
                    .city("City " + (i % 3))
                    .build();
            entityManager.persist(club);
        }
        entityManager.flush();
    }

    @Test
    void testFindAllPaginated() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Club> page = clubRepository.findAll(pageable);
        
        assertEquals(5, page.getContent().size());
        assertEquals(15, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertFalse(page.isFirst());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Club> page = clubRepository.findByNameContainingIgnoreCase("Club", pageable);
        
        assertEquals(10, page.getContent().size());
        assertEquals(15, page.getTotalElements());
    }

    @Test
    void testFindByNameContainingIgnoreCaseNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Club> page = clubRepository.findByNameContainingIgnoreCase("NotFound", pageable);
        
        assertEquals(0, page.getContent().size());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void testPaginationSecondPage() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<Club> page = clubRepository.findAll(pageable);
        
        assertEquals(5, page.getContent().size());
        assertEquals(2, page.getNumber());
    }
}