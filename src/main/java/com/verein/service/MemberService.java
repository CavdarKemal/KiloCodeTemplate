package com.verein.service;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MemberService {
    MemberResponse createMember(MemberRequest request);
    MemberResponse getMemberById(Long id);
    List<MemberResponse> getAllMembers();
    List<MemberResponse> getMembersByClub(Long clubId);
    PagedResponse<MemberResponse> getMembersPaginated(Pageable pageable);
    PagedResponse<MemberResponse> getMembersByClubPaginated(Long clubId, Pageable pageable);
    PagedResponse<MemberResponse> searchMembers(String searchTerm, Pageable pageable);
    PagedResponse<MemberResponse> getMembersByStatus(MembershipStatus status, Pageable pageable);
    PagedResponse<MemberResponse> getMembersByType(MembershipType type, Pageable pageable);
    MemberResponse updateMember(Long id, MemberRequest request);
    void deleteMember(Long id);
}