package com.verein.service;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import java.util.List;

public interface MemberService {
    MemberResponse createMember(MemberRequest request);
    MemberResponse getMemberById(Long id);
    List<MemberResponse> getAllMembers();
    List<MemberResponse> getMembersByClub(Long clubId);
    MemberResponse updateMember(Long id, MemberRequest request);
    void deleteMember(Long id);
}