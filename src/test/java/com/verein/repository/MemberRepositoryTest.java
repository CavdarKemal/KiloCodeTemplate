package com.verein.repository;

import com.verein.entity.Club;
import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByClubId_Success() {
        Club club = Club.builder().name("Test Club").build();
        entityManager.persist(club);

        Member member1 = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .club(club)
                .build();
        entityManager.persist(member1);

        Member member2 = Member.builder()
                .firstName("Anna")
                .lastName("Musterfrau")
                .email("anna@test.de")
                .club(club)
                .build();
        entityManager.persist(member2);
        entityManager.flush();

        List<Member> members = memberRepository.findByClubId(club.getId());

        assertEquals(2, members.size());
    }

    @Test
    void findByEmail_Success() {
        Club club = Club.builder().name("Test Club").build();
        entityManager.persist(club);

        Member member = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .club(club)
                .build();
        entityManager.persist(member);
        entityManager.flush();

        Member found = memberRepository.findByEmail("max@test.de").orElse(null);

        assertNotNull(found);
        assertEquals("Max", found.getFirstName());
    }

    @Test
    void existsByEmail_Success() {
        Club club = Club.builder().name("Test Club").build();
        entityManager.persist(club);

        Member member = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("exists@test.de")
                .club(club)
                .build();
        entityManager.persist(member);
        entityManager.flush();

        boolean exists = memberRepository.existsByEmail("exists@test.de");

        assertTrue(exists);
    }

    @Test
    void findByStatus_Success() {
        Club club = Club.builder().name("Test Club").build();
        entityManager.persist(club);

        Member member = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("active@test.de")
                .status(MembershipStatus.ACTIVE)
                .club(club)
                .build();
        entityManager.persist(member);
        entityManager.flush();

        List<Member> activeMembers = memberRepository.findByStatus(MembershipStatus.ACTIVE);

        assertEquals(1, activeMembers.size());
        assertEquals("ACTIVE", activeMembers.get(0).getStatus().name());
    }

    @Test
    void findByMembershipType_Success() {
        Club club = Club.builder().name("Test Club").build();
        entityManager.persist(club);

        Member member = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("senior@test.de")
                .membershipType(MembershipType.SENIOR)
                .club(club)
                .build();
        entityManager.persist(member);
        entityManager.flush();

        List<Member> seniorMembers = memberRepository.findByMembershipType(MembershipType.SENIOR);

        assertEquals(1, seniorMembers.size());
        assertEquals("SENIOR", seniorMembers.get(0).getMembershipType().name());
    }

    @Test
    void saveAndDelete_Success() {
        Club club = Club.builder().name("Test Club").build();
        entityManager.persist(club);

        Member member = Member.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("delete@test.de")
                .club(club)
                .build();
        Member saved = memberRepository.save(member);

        assertNotNull(saved.getId());

        memberRepository.deleteById(saved.getId());

        assertFalse(memberRepository.existsById(saved.getId()));
    }
}