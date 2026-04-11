package com.verein.controller;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ClubResponse> createClub(@Valid @RequestBody ClubRequest request) {
        return new ResponseEntity<>(clubService.createClub(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubResponse> getClub(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ClubResponse> updateClub(@PathVariable Long id, @Valid @RequestBody ClubRequest request) {
        return ResponseEntity.ok(clubService.updateClub(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }
}