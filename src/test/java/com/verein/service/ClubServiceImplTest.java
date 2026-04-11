package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.entity.Club;
import com.verein.repository.ClubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceImplTest {

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private ClubServiceImpl clubService;

    private Club testClub;
    private ClubRequest clubRequest;

    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1L)
                .name("Test Club")
                .description("Test Description")
                .foundedDate(LocalDate.of(2020, 1, 1))
                .city("Test City")
                .build();

        clubRequest = ClubRequest.builder()
                .name("Test Club")
                .description("Test Description")
                .foundedDate(LocalDate.of(2020, 1, 1))
                .city("Test City")
                .build();
    }

    @Test
    void createClub_Success() {
        when(clubRepository.save(any(Club.class))).thenReturn(testClub);

        ClubResponse response = clubService.createClub(clubRequest);

        assertNotNull(response);
        assertEquals("Test Club", response.getName());
        verify(clubRepository, times(1)).save(any(Club.class));
    }

    @Test
    void getClubById_Success() {
        when(clubRepository.findById(1L)).thenReturn(Optional.of(testClub));

        ClubResponse response = clubService.getClubById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Club", response.getName());
    }

    @Test
    void getClubById_NotFound() {
        when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clubService.getClubById(1L));
    }

    @Test
    void getAllClubs_Success() {
        List<Club> clubs = Arrays.asList(testClub);
        when(clubRepository.findAll()).thenReturn(clubs);

        List<ClubResponse> responses = clubService.getAllClubs();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Club", responses.get(0).getName());
    }

    @Test
    void updateClub_Success() {
        Club updatedClub = Club.builder()
                .id(1L)
                .name("Updated Club")
                .description("Updated Description")
                .foundedDate(LocalDate.of(2021, 1, 1))
                .city("Updated City")
                .build();

        when(clubRepository.findById(1L)).thenReturn(Optional.of(testClub));
        when(clubRepository.save(any(Club.class))).thenReturn(updatedClub);

        ClubRequest updateRequest = ClubRequest.builder()
                .name("Updated Club")
                .description("Updated Description")
                .foundedDate(LocalDate.of(2021, 1, 1))
                .city("Updated City")
                .build();

        ClubResponse response = clubService.updateClub(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Club", response.getName());
    }

    @Test
    void deleteClub_Success() {
        when(clubRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clubRepository).deleteById(1L);

        clubService.deleteClub(1L, "testuser");

        verify(clubRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteClub_NotFound() {
        when(clubRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> clubService.deleteClub(1L, "testuser"));
    }
}