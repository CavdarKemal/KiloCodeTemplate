package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ClubService {
    ClubResponse createClub(ClubRequest request);
    ClubResponse getClubById(Long id);
    List<ClubResponse> getAllClubs();
    PagedResponse<ClubResponse> getClubsPaginated(Pageable pageable);
    PagedResponse<ClubResponse> searchClubs(String searchTerm, Pageable pageable);
    List<ClubResponse> getDeletedClubs();
    ClubResponse updateClub(Long id, ClubRequest request);
    void deleteClub(Long id, String deletedBy);
    ClubResponse restoreClub(Long id);
}