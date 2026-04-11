package com.verein.repository;

import com.verein.entity.Member;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
    
    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NULL")
    List<Member> findAllActive();
    
    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NULL")
    Page<Member> findAllActive(Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Member> findByIdActive(Long id);
    
    @Query("SELECT m FROM Member m WHERE m.club.id = :clubId AND m.deletedAt IS NULL")
    List<Member> findByClubIdActive(Long clubId);
    
    @Query("SELECT m FROM Member m WHERE m.club.id = :clubId AND m.deletedAt IS NULL")
    Page<Member> findByClubIdActive(Long clubId, Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NOT NULL")
    List<Member> findAllDeleted();
    
    @Query("SELECT m FROM Member m WHERE m.lastName LIKE %:lastName% AND m.deletedAt IS NULL")
    Page<Member> findByLastNameContainingIgnoreCaseActive(String lastName, Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE m.status = :status AND m.deletedAt IS NULL")
    Page<Member> findByStatusActive(MembershipStatus status, Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE m.membershipType = :type AND m.deletedAt IS NULL")
    Page<Member> findByMembershipTypeActive(MembershipType type, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Member m SET m.deletedAt = CURRENT_TIMESTAMP, m.deletedBy = :deletedBy WHERE m.id = :id")
    void softDeleteById(Long id, String deletedBy);
    
    @Modifying
    @Query("UPDATE Member m SET m.deletedAt = NULL, m.deletedBy = NULL WHERE m.id = :id")
    void restoreById(Long id);
}