package com.verein.service;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.entity.Club;
import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.repository.ClubRepository;
import com.verein.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;

    @Override
    public MemberResponse createMember(MemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email bereits vorhanden: " + request.getEmail());
        }
        
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club nicht gefunden: " + request.getClubId()));

        Member member = Member.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .membershipDate(request.getMembershipDate() != null ? request.getMembershipDate() : java.time.LocalDate.now())
                .membershipType(request.getMembershipType())
                .status(request.getStatus() != null ? request.getStatus() : MembershipStatus.ACTIVE)
                .club(club)
                .build();
        
        member = memberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mitglied nicht gefunden: " + id));
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByClub(Long clubId) {
        return memberRepository.findByClubId(clubId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mitglied nicht gefunden: " + id));

        if (!member.getEmail().equals(request.getEmail()) && memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email bereits vorhanden: " + request.getEmail());
        }

        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club nicht gefunden: " + request.getClubId()));

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setBirthDate(request.getBirthDate());
        member.setGender(request.getGender());
        member.setMembershipDate(request.getMembershipDate());
        member.setMembershipType(request.getMembershipType());
        member.setStatus(request.getStatus());
        member.setClub(club);

        member = memberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Mitglied nicht gefunden: " + id);
        }
        memberRepository.deleteById(id);
    }

    private MemberResponse mapToResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .membershipDate(member.getMembershipDate())
                .membershipType(member.getMembershipType())
                .status(member.getStatus())
                .clubId(member.getClub().getId())
                .clubName(member.getClub().getName())
                .build();
    }
}