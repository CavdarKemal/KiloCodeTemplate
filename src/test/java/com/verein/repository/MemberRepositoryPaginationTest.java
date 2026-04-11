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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberRepositoryPaginationTest {

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
                .build();
        club = clubRepository.save(club);

        for (int i = 1; i <= 15; i++) {
            Member member = Member.builder()
                    .firstName("FirstName" + i)
                    .lastName("LastName" + i)
                    .email("member" + i + "@test.com")
                    .membershipType(i % 2 == 0 ? MembershipType.REGULAR : MembershipType.STUDENT)
                    .status(i <= 10 ? MembershipStatus.ACTIVE : MembershipStatus.INACTIVE)
                    .club(club)
                    .build();
            entityManager.persist(member);
        }
        entityManager.flush();
    }

    @Test
    void testFindAllPaginated() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Member> page = memberRepository.findAll(pageable);
        
        assertEquals(5, page.getContent().size());
        assertEquals(15, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void testFindByClubIdPaginated() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Member> page = memberRepository.findByClubId(club.getId(), pageable);
        
        assertEquals(5, page.getContent().size());
        assertEquals(15, page.getTotalElements());
    }

    @Test
    void testFindByStatusPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page = memberRepository.findByStatus(MembershipStatus.ACTIVE, pageable);
        
        assertEquals(10, page.getContent().size());
        assertEquals(10, page.getTotalElements());
    }

    @Test
    void testFindByMembershipTypePaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page = memberRepository.findByMembershipType(MembershipType.REGULAR, pageable);
        
        assertEquals(7, page.getContent().size());
        assertEquals(7, page.getTotalElements());
    }

    @Test
    void testFindByLastNameContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page = memberRepository.findByLastNameContainingIgnoreCase("LastName", pageable);
        
        assertEquals(10, page.getContent().size());
        assertEquals(15, page.getTotalElements());
    }
}