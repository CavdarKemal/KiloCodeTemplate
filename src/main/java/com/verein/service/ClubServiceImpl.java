package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.entity.Club;
import com.verein.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;

    @Override
    public ClubResponse createClub(ClubRequest request) {
        Club club = Club.builder()
                .name(request.getName())
                .description(request.getDescription())
                .foundedDate(request.getFoundedDate())
                .city(request.getCity())
                .build();
        club = clubRepository.save(club);
        return mapToResponse(club);
    }

    @Override
    @Transactional(readOnly = true)
    public ClubResponse getClubById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club nicht gefunden: " + id));
        return mapToResponse(club);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubResponse> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClubResponse updateClub(Long id, ClubRequest request) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club nicht gefunden: " + id));
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setFoundedDate(request.getFoundedDate());
        club.setCity(request.getCity());
        club = clubRepository.save(club);
        return mapToResponse(club);
    }

    @Override
    public void deleteClub(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new RuntimeException("Club nicht gefunden: " + id);
        }
        clubRepository.deleteById(id);
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