package com.verein.controller;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.dto.PagedResponse;
import com.verein.service.ClubService;
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
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "Clubs", description = "Vereinsverwaltung Endpoints")
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Verein erstellen", description = "Erstellt einen neuen Verein (nur ADMIN)")
    @ApiResponse(responseCode = "201", description = "Verein erfolgreich erstellt", 
        content = @Content(schema = @Schema(implementation = ClubResponse.class)))
    @ApiResponse(responseCode = "403", description = "Zugriff verweigert")
    public ResponseEntity<ClubResponse> createClub(@Valid @RequestBody ClubRequest request) {
        return new ResponseEntity<>(clubService.createClub(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Verein abrufen", description = "Gibt einen Verein nach ID zurück")
    @ApiResponse(responseCode = "200", description = "Verein gefunden", 
        content = @Content(schema = @Schema(implementation = ClubResponse.class)))
    @ApiResponse(responseCode = "404", description = "Verein nicht gefunden")
    public ResponseEntity<ClubResponse> getClub(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    @GetMapping
    @Operation(summary = "Alle Vereine abrufen", description = "Gibt alle aktiven Vereine zurück")
    @ApiResponse(responseCode = "200", description = "Liste der Vereine")
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Vereine paginiert abrufen", description = "Gibt Vereine mit Pagination zurück")
    @ApiResponse(responseCode = "200", description = "Paginierten Liste der Vereine")
    public ResponseEntity<PagedResponse<ClubResponse>> getClubsPaginated(
            @Parameter(description = "Seitennummer (0-basiert)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Seitengröße") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sortierfeld") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sortierrichtung") @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(clubService.getClubsPaginated(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Vereine suchen", description = "Sucht Vereine nach Namen")
    @ApiResponse(responseCode = "200", description = "Suchergebnisse")
    public ResponseEntity<PagedResponse<ClubResponse>> searchClubs(
            @Parameter(description = "Suchbegriff") @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(clubService.searchClubs(search, pageable));
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Gelöschte Vereine abrufen", description = "Gibt alle soft-gelöschten Vereine zurück (nur ADMIN)")
    @ApiResponse(responseCode = "200", description = "Liste der gelöschten Vereine")
    public ResponseEntity<List<ClubResponse>> getDeletedClubs() {
        return ResponseEntity.ok(clubService.getDeletedClubs());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Verein aktualisieren", description = "Aktualisiert einen Verein (nur ADMIN)")
    @ApiResponse(responseCode = "200", description = "Verein aktualisiert", 
        content = @Content(schema = @Schema(implementation = ClubResponse.class)))
    @ApiResponse(responseCode = "404", description = "Verein nicht gefunden")
    public ResponseEntity<ClubResponse> updateClub(@PathVariable Long id, @Valid @RequestBody ClubRequest request) {
        return ResponseEntity.ok(clubService.updateClub(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Verein löschen", description = "Soft-Delete eines Vereins (nur ADMIN)")
    @ApiResponse(responseCode = "204", description = "Verein erfolgreich gelöscht")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id, Authentication auth) {
        clubService.deleteClub(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Verein wiederherstellen", description = "Stellt einen gelöschten Verein wieder her (nur ADMIN)")
    @ApiResponse(responseCode = "200", description = "Verein wiederhergestellt", 
        content = @Content(schema = @Schema(implementation = ClubResponse.class)))
    public ResponseEntity<ClubResponse> restoreClub(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.restoreClub(id));
    }
}