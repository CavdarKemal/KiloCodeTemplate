package com.verein.repository;

import com.verein.entity.Club;
import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
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
class MemberRepositorySoftDeleteTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    private Club club;

    @BeforeEach
    void setUp() {
        club = Club.builder()
                .name("FC Berlin")
                .city("Berlin")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        club = clubRepository.save(club);

        Member member1 = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.com")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .club(club)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(member1);

        Member member2 = Member.builder()
                .firstName("Anna")
                .lastName("Smith")
                .email("anna@test.com")
                .membershipType(MembershipType.STUDENT)
                .status(MembershipStatus.INACTIVE)
                .club(club)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .deletedBy("admin")
                .build();
        entityManager.persist(member2);

        entityManager.flush();
    }

    @Test
    void testFindAllActiveReturnsOnlyNonDeleted() {
        List<Member> result = memberRepository.findAllActive();
        
        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).getFirstName());
    }

    @Test
    void testFindAllDeletedReturnsOnlyDeleted() {
        List<Member> result = memberRepository.findAllDeleted();
        
        assertEquals(1, result.size());
        assertEquals("Anna", result.get(0).getFirstName());
    }

    @Test
    void testFindByIdActiveReturnsOnlyNonDeleted() {
        Member active = memberRepository.findAllActive().get(0);
        Optional<Member> result = memberRepository.findByIdActive(active.getId());
        
        assertTrue(result.isPresent());
        assertEquals("Max", result.get().getFirstName());
    }

    @Test
    void testFindByIdActiveReturnsEmptyForDeleted() {
        List<Member> deleted = memberRepository.findAllDeleted();
        Optional<Member> result = memberRepository.findByIdActive(deleted.get(0).getId());
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByClubIdActiveReturnsOnlyNonDeleted() {
        List<Member> result = memberRepository.findByClubIdActive(club.getId());
        
        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).getFirstName());
    }

    @Test
    void testSoftDelete() {
        Member member = memberRepository.findAllActive().get(0);
        memberRepository.softDeleteById(member.getId(), "testuser");
        
        Optional<Member> result = memberRepository.findByIdActive(member.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void testRestoreById() {
        Member deleted = memberRepository.findAllDeleted().get(0);
        memberRepository.restoreById(deleted.getId());
        
        Optional<Member> result = memberRepository.findByIdActive(deleted.getId());
        assertTrue(result.isPresent());
    }
}