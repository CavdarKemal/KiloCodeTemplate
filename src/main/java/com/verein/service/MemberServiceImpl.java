package com.verein.service;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.Club;
import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import com.verein.exception.DuplicateResourceException;
import com.verein.exception.ResourceNotFoundException;
import com.verein.repository.ClubRepository;
import com.verein.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
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
            throw new DuplicateResourceException("Member", "email", request.getEmail());
        }
        
        Club club = clubRepository.findByIdActive(request.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club", request.getClubId()));

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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        member = memberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByClub(Long clubId) {
        return memberRepository.findByClubIdActive(clubId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MemberResponse> getMembersPaginated(Pageable pageable) {
        Page<Member> page = memberRepository.findAllActive(pageable);
        List<MemberResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MemberResponse> getMembersByClubPaginated(Long clubId, Pageable pageable) {
        Page<Member> page = memberRepository.findByClubIdActive(clubId, pageable);
        List<MemberResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MemberResponse> searchMembers(String searchTerm, Pageable pageable) {
        Page<Member> page = memberRepository.findByLastNameContainingIgnoreCaseActive(searchTerm, pageable);
        List<MemberResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MemberResponse> getMembersByStatus(MembershipStatus status, Pageable pageable) {
        Page<Member> page = memberRepository.findByStatusActive(status, pageable);
        List<MemberResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MemberResponse> getMembersByType(MembershipType type, Pageable pageable) {
        Page<Member> page = memberRepository.findByMembershipTypeActive(type, pageable);
        List<MemberResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getDeletedMembers() {
        return memberRepository.findAllDeleted().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));

        if (!member.getEmail().equals(request.getEmail()) && memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Member", "email", request.getEmail());
        }

        Club club = clubRepository.findByIdActive(request.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club", request.getClubId()));

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
        member.setUpdatedAt(LocalDateTime.now());

        member = memberRepository.save(member);
        return mapToResponse(member);
    }

    @Override
    public void deleteMember(Long id, String deletedBy) {
        Member member = memberRepository.findByIdActive(id)
                .orElseThrow(() -> new RuntimeException("Mitglied nicht gefunden: " + id));
        member.softDelete(deletedBy);
        memberRepository.save(member);
    }

    @Override
    public MemberResponse restoreMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        if (!member.isDeleted()) {
            throw new IllegalArgumentException("Mitglied ist nicht gelöscht: " + id);
        }
        member.restore();
        member.setUpdatedAt(LocalDateTime.now());
        member = memberRepository.save(member);
        return mapToResponse(member);
    }
    
    @Override
    public CsvImportResult importMembersFromCsv(String csvData) {
        if (csvData == null || csvData.isBlank()) {
            return new CsvImportResult(0, 0, List.of("CSV-Daten sind leer"));
        }
        
        String[] lines = csvData.split("\n");
        int imported = 0;
        int failed = 0;
        List<String> errors = new java.util.ArrayList<>();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            if (i == 0 && line.toLowerCase().contains("firstname")) {
                continue;
            }
            
            try {
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    errors.add("Zeile " + (i+1) + ": Zu wenig Spalten");
                    failed++;
                    continue;
                }
                
                MemberRequest request = new MemberRequest();
                request.setFirstName(parts[0].trim());
                request.setLastName(parts[1].trim());
                request.setEmail(parts[2].trim());
                request.setPhoneNumber(parts.length > 3 ? parts[3].trim() : null);
                request.setGender(parts.length > 4 ? parts[4].trim() : null);
                request.setMembershipType(MembershipType.valueOf(parts[5].trim().toUpperCase()));
                
                if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                    request.setClubId(Long.parseLong(parts[6].trim()));
                }
                
                if (request.getClubId() == null) {
                    errors.add("Zeile " + (i+1) + ": Keine Club-ID angegeben");
                    failed++;
                    continue;
                }
                
                createMember(request);
                imported++;
                
            } catch (Exception e) {
                errors.add("Zeile " + (i+1) + ": " + e.getMessage());
                failed++;
            }
        }
        
        return new CsvImportResult(imported, failed, errors);
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