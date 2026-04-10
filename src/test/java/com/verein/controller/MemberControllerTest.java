package com.verein.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import com.verein.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createMember_Success() throws Exception {
        MemberRequest request = MemberRequest.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .phoneNumber("123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender("M")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .clubId(1L)
                .build();

        MemberResponse response = MemberResponse.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .phoneNumber("123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender("M")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .clubId(1L)
                .clubName("Test Club")
                .build();

        when(memberService.createMember(any(MemberRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Max"))
                .andExpect(jsonPath("$.email").value("max@test.de"));
    }

    @Test
    void getMember_Success() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("max@test.de")
                .clubId(1L)
                .clubName("Test Club")
                .build();

        when(memberService.getMemberById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Max"));
    }

    @Test
    void getAllMembers_Success() throws Exception {
        MemberResponse member1 = MemberResponse.builder().id(1L).firstName("Max").lastName("Mustermann").clubId(1L).clubName("Club 1").build();
        MemberResponse member2 = MemberResponse.builder().id(2L).firstName("Anna").lastName("Musterfrau").clubId(1L).clubName("Club 1").build();

        List<MemberResponse> members = Arrays.asList(member1, member2);
        when(memberService.getAllMembers()).thenReturn(members);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Max"))
                .andExpect(jsonPath("$[1].firstName").value("Anna"));
    }

    @Test
    void getMembersByClub_Success() throws Exception {
        MemberResponse member = MemberResponse.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .clubId(1L)
                .clubName("Test Club")
                .build();

        List<MemberResponse> members = Arrays.asList(member);
        when(memberService.getMembersByClub(1L)).thenReturn(members);

        mockMvc.perform(get("/api/members/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Max"));
    }

    @Test
    void updateMember_Success() throws Exception {
        MemberRequest request = MemberRequest.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("max.new@test.de")
                .membershipType(MembershipType.REGULAR)
                .status(MembershipStatus.ACTIVE)
                .clubId(1L)
                .build();

        MemberResponse response = MemberResponse.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("max.new@test.de")
                .membershipType(MembershipType.REGULAR)
                .clubId(1L)
                .build();

        when(memberService.updateMember(eq(1L), any(MemberRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("max.new@test.de"));
    }

    @Test
    void deleteMember_Success() throws Exception {
        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());
    }
}