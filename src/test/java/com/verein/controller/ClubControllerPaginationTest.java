package com.verein.controller;

import com.verein.dto.ClubResponse;
import com.verein.dto.PagedResponse;
import com.verein.service.ClubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any Pageable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubController.class)
class ClubControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClubService clubService;

    @Test
    @WithMockUser(roles = "USER")
    void testGetClubsPaginated() throws Exception {
        ClubResponse club1 = ClubResponse.builder()
                .id(1L)
                .name("FC Berlin")
                .city("Berlin")
                .build();
        
        ClubResponse club2 = ClubResponse.builder()
                .id(2L)
                .name("FC Hamburg")
                .city("Hamburg")
                .build();

        PagedResponse<ClubResponse> pagedResponse = new PagedResponse<>(
                List.of(club1, club2), 0, 10, 2, 1, true);

        when(clubService.getClubsPaginated(any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/clubs/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSearchClubs() throws Exception {
        ClubResponse club = ClubResponse.builder()
                .id(1L)
                .name("FC Berlin")
                .city("Berlin")
                .build();

        PagedResponse<ClubResponse> pagedResponse = new PagedResponse<>(
                List.of(club), 0, 10, 1, 1, true);

        when(clubService.searchClubs(any String, any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/clubs/search")
                .param("search", "Berlin")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("FC Berlin"));
    }

    @Test
    void testGetClubsPaginatedUnauthorized() throws Exception {
        mockMvc.perform(get("/api/clubs/paginated"))
                .andExpect(status().isUnauthorized());
    }
}