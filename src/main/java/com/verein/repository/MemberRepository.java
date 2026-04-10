package com.verein.repository;

import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByClubId(Long clubId);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Member> findByStatus(MembershipStatus status);
    List<Member> findByMembershipType(MembershipType membershipType);
}