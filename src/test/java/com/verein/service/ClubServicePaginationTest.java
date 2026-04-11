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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClubServicePaginationTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ClubServiceImpl clubService;

    private Club club1;
    private Club club2;

    @BeforeEach
    void setUp() {
        club1 = Club.builder()
                .id(1L)
                .name("FC Berlin")
                .description("Sportverein")
                .city("Berlin")
                .build();
        
        club2 = Club.builder()
                .id(2L)
                .name("FC Hamburg")
                .description("Fussballverein")
                .city("Hamburg")
                .build();
    }

    @Test
    void testGetClubsPaginated() {
        List<Club> clubs = Arrays.asList(club1, club2);
        Page<Club> clubPage = new PageImpl<>(clubs, PageRequest.of(0, 10), clubs.size());
        
        when(clubRepository.findAll(any(Pageable.class))).thenReturn(clubPage);
        
        PagedResponse<ClubResponse> result = clubService.getClubsPaginated(PageRequest.of(0, 10));
        
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testSearchClubs() {
        List<Club> clubs = Arrays.asList(club1);
        Page<Club> clubPage = new PageImpl<>(clubs, PageRequest.of(0, 10), clubs.size());
        
        when(clubRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(clubPage);
        
        PagedResponse<ClubResponse> result = clubService.searchClubs("Berlin", PageRequest.of(0, 10));
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("FC Berlin", result.getContent().get(0).getName());
    }

    @Test
    void testSearchClubsNoResults() {
        Page<Club> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        
        when(clubRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(emptyPage);
        
        PagedResponse<ClubResponse> result = clubService.searchClubs("NotFound", PageRequest.of(0, 10));
        
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
    }
}