package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import java.util.List;

public interface ClubService {
    ClubResponse createClub(ClubRequest request);
    ClubResponse getClubById(Long id);
    List<ClubResponse> getAllClubs();
    ClubResponse updateClub(Long id, ClubRequest request);
    void deleteClub(Long id);
}