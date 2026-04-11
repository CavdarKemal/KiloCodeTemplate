package com.verein.controller;

import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.MembershipStatus;
import com.verein.entity.MembershipType;
import com.verein.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Mitgliederverwaltung Endpoints")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Mitglied erstellen", description = "Erstellt ein neues Mitglied (nur ADMIN)")
    @ApiResponse(responseCode = "201", description = "Mitglied erfolgreich erstellt", 
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        return new ResponseEntity<>(memberService.createMember(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/paginated")
    public ResponseEntity<PagedResponse<MemberResponse>> getMembersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(memberService.getMembersPaginated(pageable));
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<MemberResponse>> getMembersByClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(memberService.getMembersByClub(clubId));
    }

    @GetMapping("/club/{clubId}/paginated")
    public ResponseEntity<PagedResponse<MemberResponse>> getMembersByClubPaginated(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(memberService.getMembersByClubPaginated(clubId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<MemberResponse>> searchMembers(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(memberService.searchMembers(search, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PagedResponse<MemberResponse>> getMembersByStatus(
            @PathVariable MembershipStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(memberService.getMembersByStatus(status, pageable));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<PagedResponse<MemberResponse>> getMembersByType(
            @PathVariable MembershipType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(memberService.getMembersByType(type, pageable));
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MemberResponse>> getDeletedMembers() {
        return ResponseEntity.ok(memberService.getDeletedMembers());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id, Authentication auth) {
        memberService.deleteMember(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberResponse> restoreMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.restoreMember(id));
    }
}