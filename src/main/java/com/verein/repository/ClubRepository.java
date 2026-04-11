package com.verein.repository;

import com.verein.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByName(String name);
    boolean existsByName(String name);
    Page<Club> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT c FROM Club c WHERE c.deletedAt IS NULL")
    List<Club> findAllActive();
    
    @Query("SELECT c FROM Club c WHERE c.deletedAt IS NULL")
    Page<Club> findAllActive(Pageable pageable);
    
    @Query("SELECT c FROM Club c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Club> findByIdActive(Long id);
    
    @Query("SELECT c FROM Club c WHERE c.deletedAt IS NOT NULL")
    List<Club> findAllDeleted();
    
    @Query("SELECT c FROM Club c WHERE c.name LIKE %:name% AND c.deletedAt IS NULL")
    Page<Club> findByNameContainingIgnoreCaseActive(String name, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Club c SET c.deletedAt = CURRENT_TIMESTAMP, c.deletedBy = :deletedBy WHERE c.id = :id")
    void softDeleteById(Long id, String deletedBy);
    
    @Modifying
    @Query("UPDATE Club c SET c.deletedAt = NULL, c.deletedBy = NULL WHERE c.id = :id")
    void restoreById(Long id);
}