package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.Club;
import com.verein.exception.DuplicateResourceException;
import com.verein.exception.ResourceNotFoundException;
import com.verein.repository.ClubRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    public ClubResponse createClub(ClubRequest request) {
        if (clubRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Club", "name", request.getName());
        }
        
        Club club = Club.builder()
                .name(request.getName())
                .description(request.getDescription())
                .foundedDate(request.getFoundedDate())
                .city(request.getCity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        club = clubRepository.save(club);
        return mapToResponse(club);
    }

    @Override
    @Transactional(readOnly = true)
    public ClubResponse getClubById(Long id) {
        Club club = clubRepository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club", id));
        return mapToResponse(club);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubResponse> getAllClubs() {
        return clubRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ClubResponse> getClubsPaginated(Pageable pageable) {
        Page<Club> page = clubRepository.findAllActive(pageable);
        List<ClubResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ClubResponse> searchClubs(String searchTerm, Pageable pageable) {
        Page<Club> page = clubRepository.findByNameContainingIgnoreCaseActive(searchTerm, pageable);
        List<ClubResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubResponse> getDeletedClubs() {
        return clubRepository.findAllDeleted().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClubResponse updateClub(Long id, ClubRequest request) {
        Club club = clubRepository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club", id));
        
        try {
            String oldValue = objectMapper.writeValueAsString(mapToResponse(club));
            club.setName(request.getName());
            club.setDescription(request.getDescription());
            club.setFoundedDate(request.getFoundedDate());
            club.setCity(request.getCity());
            club.setUpdatedAt(LocalDateTime.now());
            club = clubRepository.save(club);
            String newValue = objectMapper.writeValueAsString(mapToResponse(club));
            auditService.logUpdate("Club", id, oldValue, newValue, "system");
        } catch (Exception e) {
            club = clubRepository.save(club);
        }
        return mapToResponse(club);
    }

    @Override
    public void deleteClub(Long id, String deletedBy) {
        Club club = clubRepository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club", id));
        club.softDelete(deletedBy);
        clubRepository.save(club);
        auditService.logDelete("Club", id, club.getName(), deletedBy);
    }

    @Override
    public ClubResponse restoreClub(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club", id));
        if (!club.isDeleted()) {
            throw new IllegalArgumentException("Club ist nicht gelöscht: " + id);
        }
        club.restore();
        club.setUpdatedAt(LocalDateTime.now());
        club = clubRepository.save(club);
        auditService.logRestore("Club", id, "system");
        return mapToResponse(club);
    }

    private ClubResponse mapToResponse(Club club) {
        return ClubResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .foundedDate(club.getFoundedDate())
                .city(club.getCity())
                .memberCount(club.getMembers() != null ? club.getMembers().size() : 0)
                .build();
    }
}