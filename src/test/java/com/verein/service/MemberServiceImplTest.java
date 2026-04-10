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
        when(clubRepository.findById(1L)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByEmail("max@test.de")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse response = memberService.createMember(memberRequest);

        assertNotNull(response);
        assertEquals("Max", response.getFirstName());
        assertEquals("Mustermann", response.getLastName());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void createMember_DuplicateEmail() {
        when(memberRepository.existsByEmail("max@test.de")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> memberService.createMember(memberRequest));
    }

    @Test
    void createMember_ClubNotFound() {
        when(memberRepository.existsByEmail("max@test.de")).thenReturn(false);
        when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.createMember(memberRequest));
    }

    @Test
    void getMemberById_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        MemberResponse response = memberService.getMemberById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Max", response.getFirstName());
    }

    @Test
    void getMemberById_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> memberService.getMemberById(1L));
    }

    @Test
    void getAllMembers_Success() {
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findAll()).thenReturn(members);

        List<MemberResponse> responses = memberService.getAllMembers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Max", responses.get(0).getFirstName());
    }

    @Test
    void getMembersByClub_Success() {
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findByClubId(1L)).thenReturn(members);

        List<MemberResponse> responses = memberService.getMembersByClub(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getClubId());
    }

    @Test
    void updateMember_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(clubRepository.findById(1L)).thenReturn(Optional.of(testClub));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse response = memberService.updateMember(1L, memberRequest);

        assertNotNull(response);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void deleteMember_Success() {
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMember_NotFound() {
        when(memberRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> memberService.deleteMember(1L));
    }
}