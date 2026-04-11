package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.Club;
import com.verein.repository.ClubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClubServiceSoftDeleteTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ClubServiceImpl clubService;

    private Club activeClub;
    private Club deletedClub;

    @BeforeEach
    void setUp() {
        activeClub = Club.builder()
                .id(1L)
                .name("FC Berlin")
                .description("Sportverein")
                .city("Berlin")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
        
        deletedClub = Club.builder()
                .id(2L)
                .name("FC Hamburg")
                .description("Fussballverein")
                .city("Hamburg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .deletedBy("admin")
                .build();
    }

    @Test
    void testGetAllClubsReturnsOnlyActive() {
        when(clubRepository.findAllActive()).thenReturn(List.of(activeClub));
        
        List<ClubResponse> result = clubService.getAllClubs();
        
        assertEquals(1, result.size());
        assertEquals("FC Berlin", result.get(0).getName());
    }

    @Test
    void testGetDeletedClubs() {
        when(clubRepository.findAllDeleted()).thenReturn(List.of(deletedClub));
        
        List<ClubResponse> result = clubService.getDeletedClubs();
        
        assertEquals(1, result.size());
        assertEquals("FC Hamburg", result.get(0).getName());
    }

    @Test
    void testSoftDelete() {
        when(clubRepository.findByIdActive(1L)).thenReturn(Optional.of(activeClub));
        when(clubRepository.save(any(Club.class))).thenReturn(activeClub);
        
        clubService.deleteClub(1L, "admin");
        
        assertNotNull(activeClub.getDeletedAt());
        assertEquals("admin", activeClub.getDeletedBy());
    }

    @Test
    void testRestoreClub() {
        when(clubRepository.findById(2L)).thenReturn(Optional.of(deletedClub));
        when(clubRepository.save(any(Club.class))).thenReturn(deletedClub);
        
        ClubResponse result = clubService.restoreClub(2L);
        
        assertNotNull(result);
    }

    @Test
    void testGetClubByIdExcludesDeleted() {
        when(clubRepository.findByIdActive(1L)).thenReturn(Optional.of(activeClub));
        
        ClubResponse result = clubService.getClubById(1L);
        
        assertEquals("FC Berlin", result.getName());
    }

    @Test
    void testGetClubByIdThrowsForDeleted() {
        when(clubRepository.findByIdActive(99L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> clubService.getClubById(99L));
    }

    @Test
    void testGetClubsPaginatedReturnsOnlyActive() {
        Page<Club> page = new PageImpl<>(List.of(activeClub), PageRequest.of(0, 10), 1);
        when(clubRepository.findAllActive(any(Pageable.class))).thenReturn(page);
        
        PagedResponse<ClubResponse> result = clubService.getClubsPaginated(PageRequest.of(0, 10));
        
        assertEquals(1, result.getContent().size());
    }
}