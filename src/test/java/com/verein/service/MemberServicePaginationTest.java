package com.verein.service;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.Club;
import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import com.verein.repository.ClubRepository;
import com.verein.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServicePaginationTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Club club;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        club = Club.builder()
                .id(1L)
                .name("FC Berlin")
                .build();

        member1 = Member.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.com")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .club(club)
                .build();

        member2 = Member.builder()
                .id(2L)
                .firstName("Anna")
                .lastName("Smith")
                .email("anna@test.com")
                .membershipType(MembershipType.STUDENT)
                .status(MembershipStatus.ACTIVE)
                .club(club)
                .build();
    }

    @Test
    void testGetMembersPaginated() {
        List<Member> members = Arrays.asList(member1, member2);
        Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(0, 10), members.size());

        when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);

        PagedResponse<MemberResponse> result = memberService.getMembersPaginated(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
    }

    @Test
    void testGetMembersByClubPaginated() {
        List<Member> members = Arrays.asList(member1);
        Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(0, 10), members.size());

        when(memberRepository.findByClubId(anyLong(), any(Pageable.class))).thenReturn(memberPage);

        PagedResponse<MemberResponse> result = memberService.getMembersByClubPaginated(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testSearchMembers() {
        List<Member> members = Arrays.asList(member1);
        Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(0, 10), members.size());

        when(memberRepository.findByLastNameContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(memberPage);

        PagedResponse<MemberResponse> result = memberService.searchMembers("Muster", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Mustermann", result.getContent().get(0).getLastName());
    }

    @Test
    void testGetMembersByStatus() {
        List<Member> members = Arrays.asList(member1, member2);
        Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(0, 10), members.size());

        when(memberRepository.findByStatus(any(MembershipStatus.class), any(Pageable.class)))
            .thenReturn(memberPage);

        PagedResponse<MemberResponse> result = memberService.getMembersByStatus(
            MembershipStatus.ACTIVE, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testGetMembersByType() {
        List<Member> members = Arrays.asList(member1);
        Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(0, 10), members.size());

        when(memberRepository.findByMembershipType(any(MembershipType.class), any(Pageable.class)))
            .thenReturn(memberPage);

        PagedResponse<MemberResponse> result = memberService.getMembersByType(
            MembershipType.REGULAR, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(MembershipType.REGULAR, result.getContent().get(0).getMembershipType());
    }
}