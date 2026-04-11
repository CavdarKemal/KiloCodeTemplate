package com.verein.controller;

import com.verein.dto.PagedResponse;
import com.verein.entity.AuditLog;
import com.verein.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/entity/{entityName}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getEntityHistory(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getEntityHistory(entityName, entityId));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getUserActivity(@PathVariable String username) {
        return ResponseEntity.ok(auditService.getUserActivity(username));
    }

    @GetMapping("/type/{entityName}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getEntityTypeHistory(@PathVariable String entityName) {
        return ResponseEntity.ok(auditService.getEntityTypeHistory(entityName));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<PagedResponse<AuditLog>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("performedAt").descending());
        return ResponseEntity.ok(auditService.getAllAuditLogs(pageable));
    }

    @GetMapping("/action")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(
            @RequestParam String entityName,
            @RequestParam String action) {
        return ResponseEntity.ok(auditService.getAuditLogsByAction(entityName, action));
    }
}