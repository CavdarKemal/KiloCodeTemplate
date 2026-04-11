package com.verein.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.service.ClubService;
import com.verein.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webmvc.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubController.class)
@Import(TestSecurityConfig.class)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClubService clubService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createClub_Success() throws Exception {
        ClubRequest request = ClubRequest.builder()
                .name("Test Club")
                .description("Test Description")
                .foundedDate(LocalDate.of(2020, 1, 1))
                .city("Test City")
                .build();

        ClubResponse response = ClubResponse.builder()
                .id(1L)
                .name("Test Club")
                .description("Test Description")
                .foundedDate(LocalDate.of(2020, 1, 1))
                .city("Test City")
                .memberCount(0)
                .build();

        when(clubService.createClub(any(ClubRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/clubs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Club"));
    }

    @Test
    void getClub_Success() throws Exception {
        ClubResponse response = ClubResponse.builder()
                .id(1L)
                .name("Test Club")
                .description("Test Description")
                .city("Test City")
                .memberCount(5)
                .build();

        when(clubService.getClubById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/clubs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Club"))
                .andExpect(jsonPath("$.memberCount").value(5));
    }

    @Test
    void getAllClubs_Success() throws Exception {
        ClubResponse club1 = ClubResponse.builder().id(1L).name("Club 1").memberCount(10).build();
        ClubResponse club2 = ClubResponse.builder().id(2L).name("Club 2").memberCount(20).build();

        List<ClubResponse> clubs = Arrays.asList(club1, club2);
        when(clubService.getAllClubs()).thenReturn(clubs);

        mockMvc.perform(get("/api/clubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Club 1"))
                .andExpect(jsonPath("$[1].name").value("Club 2"));
    }

    @Test
    void updateClub_Success() throws Exception {
        ClubRequest request = ClubRequest.builder()
                .name("Updated Club")
                .description("Updated Description")
                .city("Updated City")
                .build();

        ClubResponse response = ClubResponse.builder()
                .id(1L)
                .name("Updated Club")
                .description("Updated Description")
                .city("Updated City")
                .memberCount(5)
                .build();

        when(clubService.updateClub(eq(1L), any(ClubRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/clubs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Club"));
    }

    @Test
    void deleteClub_Success() throws Exception {
        mockMvc.perform(delete("/api/clubs/1"))
                .andExpect(status().isNoContent());
    }
}