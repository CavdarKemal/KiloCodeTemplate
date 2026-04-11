package com.verein.service;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Club testClub;
    private Member testMember;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1L)
                .name("Test Club")
                .build();

        testMember = Member.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .phoneNumber("123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender("M")
                .membershipDate(LocalDate.now())
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .club(testClub)
                .build();

        memberRequest = MemberRequest.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .phoneNumber("123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender("M")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .clubId(1L)
                .build();
    }

    @Test
    void createMember_Success() {
        lenient().when(clubRepository.findById(1L)).thenReturn(Optional.of(testClub));
        lenient().when(memberRepository.existsByEmail("max@test.de")).thenReturn(false);
        lenient().when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse response = memberService.createMember(memberRequest);

        assertNotNull(response);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void createMember_DuplicateEmail() {
        lenient().when(memberRepository.existsByEmail("max@test.de")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> memberService.createMember(memberRequest));
    }

    @Test
    void createMember_ClubNotFound() {
        lenient().when(memberRepository.existsByEmail("max@test.de")).thenReturn(false);
        lenient().when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.createMember(memberRequest));
    }

    @Test
    void getMemberById_Success() {
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        MemberResponse response = memberService.getMemberById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getMemberById_NotFound() {
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.getMemberById(1L));
    }

    @Test
    void getAllMembers_Success() {
        List<Member> members = Arrays.asList(testMember);
        lenient().when(memberRepository.findAll()).thenReturn(members);

        List<MemberResponse> responses = memberService.getAllMembers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getMembersByClub_Success() {
        List<Member> members = Arrays.asList(testMember);
        lenient().when(memberRepository.findByClubId(1L)).thenReturn(members);

        List<MemberResponse> responses = memberService.getMembersByClub(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void updateMember_Success() {
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        lenient().when(clubRepository.findById(1L)).thenReturn(Optional.of(testClub));
        lenient().when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse response = memberService.updateMember(1L, memberRequest);

        assertNotNull(response);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void deleteMember_Success() {
        lenient().when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        memberService.deleteMember(1L, "testuser");

        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMember_NotFound() {
        lenient().when(memberRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> memberService.deleteMember(1L, "testuser"));
    }
}