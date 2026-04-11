package com.verein.service;

import com.verein.dto.ClubRequest;
import com.verein.dto.ClubResponse;
import com.verein.dto.MemberRequest;
import com.verein.dto.MemberResponse;
import com.verein.dto.PagedResponse;
import com.verein.entity.AuditLog;
import com.verein.repository.AuditLogRepository;
import com.verein.repository.ClubRepository;
import com.verein.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public void logCreate(String entityName, Long entityId, String newValue, String performedBy) {
        AuditLog log = AuditLog.create(entityName, entityId, "CREATE", null, newValue, performedBy);
        auditLogRepository.save(log);
    }

    public void logUpdate(String entityName, Long entityId, String oldValue, String newValue, String performedBy) {
        AuditLog log = AuditLog.create(entityName, entityId, "UPDATE", oldValue, newValue, performedBy);
        auditLogRepository.save(log);
    }

    public void logDelete(String entityName, Long entityId, String oldValue, String performedBy) {
        AuditLog log = AuditLog.create(entityName, entityId, "DELETE", oldValue, null, performedBy);
        auditLogRepository.save(log);
    }

    public void logRestore(String entityName, Long entityId, String performedBy) {
        AuditLog log = AuditLog.create(entityName, entityId, "RESTORE", null, null, performedBy);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getEntityHistory(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc(entityName, entityId);
    }

    public List<AuditLog> getUserActivity(String username) {
        return auditLogRepository.findByPerformedByOrderByPerformedAtDesc(username);
    }

    public List<AuditLog> getEntityTypeHistory(String entityName) {
        return auditLogRepository.findByEntityNameOrderByPerformedAtDesc(entityName);
    }

    public PagedResponse<AuditLog> getAllAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByPerformedAtDesc(pageable);
        return PagedResponse.of(page.getContent(), page);
    }

    public List<AuditLog> getAuditLogsByAction(String entityName, String action) {
        return auditLogRepository.findByActionAndEntityNameOrderByPerformedAtDesc(action, entityName);
    }
}