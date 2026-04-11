package com.verein.repository;

import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByClubId(Long clubId);
    Page<Member> findByClubId(Long clubId, Pageable pageable);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Member> findByStatus(MembershipStatus status);
    Page<Member> findByStatus(MembershipStatus status, Pageable pageable);
    List<Member> findByMembershipType(MembershipType membershipType);
    Page<Member> findByMembershipType(MembershipType membershipType, Pageable pageable);
    Page<Member> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
}