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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceSoftDeleteTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Club club;
    private Member activeMember;
    private Member deletedMember;

    @BeforeEach
    void setUp() {
        club = Club.builder()
                .id(1L)
                .name("FC Berlin")
                .city("Berlin")
                .deletedAt(null)
                .build();

        activeMember = Member.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.com")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .club(club)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();

        deletedMember = Member.builder()
                .id(2L)
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
    }

    @Test
    void testGetAllMembersReturnsOnlyActive() {
        when(memberRepository.findAllActive()).thenReturn(List.of(activeMember));
        
        List<MemberResponse> result = memberService.getAllMembers();
        
        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).getFirstName());
    }

    @Test
    void testGetDeletedMembers() {
        when(memberRepository.findAllDeleted()).thenReturn(List.of(deletedMember));
        
        List<MemberResponse> result = memberService.getDeletedMembers();
        
        assertEquals(1, result.size());
        assertEquals("Anna", result.get(0).getFirstName());
    }

    @Test
    void testSoftDelete() {
        when(memberRepository.findByIdActive(1L)).thenReturn(Optional.of(activeMember));
        when(memberRepository.save(any(Member.class))).thenReturn(activeMember);
        
        memberService.deleteMember(1L, "admin");
        
        assertNotNull(activeMember.getDeletedAt());
        assertEquals("admin", activeMember.getDeletedBy());
    }

    @Test
    void testRestoreMember() {
        when(memberRepository.findById(2L)).thenReturn(Optional.of(deletedMember));
        when(memberRepository.save(any(Member.class))).thenReturn(deletedMember);
        
        MemberResponse result = memberService.restoreMember(2L);
        
        assertNotNull(result);
    }

    @Test
    void testGetMemberByIdExcludesDeleted() {
        when(memberRepository.findByIdActive(1L)).thenReturn(Optional.of(activeMember));
        
        MemberResponse result = memberService.getMemberById(1L);
        
        assertEquals("Max", result.getFirstName());
    }

    @Test
    void testGetMemberByIdThrowsForDeleted() {
        when(memberRepository.findByIdActive(99L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> memberService.getMemberById(99L));
    }

    @Test
    void testGetMembersPaginatedReturnsOnlyActive() {
        Page<Member> page = new PageImpl<>(List.of(activeMember), PageRequest.of(0, 10), 1);
        when(memberRepository.findAllActive(any(Pageable.class))).thenReturn(page);
        
        PagedResponse<MemberResponse> result = memberService.getMembersPaginated(PageRequest.of(0, 10));
        
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetMembersByClubReturnsOnlyActive() {
        when(memberRepository.findByClubIdActive(1L)).thenReturn(List.of(activeMember));
        
        List<MemberResponse> result = memberService.getMembersByClub(1L);
        
        assertEquals(1, result.size());
    }
}