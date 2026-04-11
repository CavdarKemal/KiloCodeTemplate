package com.verein.service;

import com.verein.entity.AuditLog;
import com.verein.repository.AuditLogRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuditService auditService;

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = AuditLog.builder()
                .id(1L)
                .entityName("Club")
                .entityId(1L)
                .action("CREATE")
                .newValue("{\"name\":\"FC Berlin\"}")
                .performedBy("admin")
                .performedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testLogCreate() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        
        auditService.logCreate("Club", 1L, "{\"name\":\"FC Berlin\"}", "admin");
        
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogUpdate() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        
        auditService.logUpdate("Club", 1L, "{\"name\":\"Old\"}", "{\"name\":\"New\"}", "admin");
        
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogDelete() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        
        auditService.logDelete("Club", 1L, "{\"name\":\"FC Berlin\"}", "admin");
        
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogRestore() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);
        
        auditService.logRestore("Club", 1L, "admin");
        
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testGetEntityHistory() {
        when(auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc("Club", 1L))
            .thenReturn(List.of(auditLog));
        
        List<AuditLog> result = auditService.getEntityHistory("Club", 1L);
        
        assertEquals(1, result.size());
        assertEquals("Club", result.get(0).getEntityName());
    }

    @Test
    void testGetUserActivity() {
        when(auditLogRepository.findByPerformedByOrderByPerformedAtDesc("admin"))
            .thenReturn(List.of(auditLog));
        
        List<AuditLog> result = auditService.getUserActivity("admin");
        
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getPerformedBy());
    }

    @Test
    void testGetEntityTypeHistory() {
        when(auditLogRepository.findByEntityNameOrderByPerformedAtDesc("Club"))
            .thenReturn(List.of(auditLog));
        
        List<AuditLog> result = auditService.getEntityTypeHistory("Club");
        
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllAuditLogs() {
        Page<AuditLog> page = new PageImpl<>(List.of(auditLog), PageRequest.of(0, 10), 1);
        when(auditLogRepository.findAllByOrderByPerformedAtDesc(any(Pageable.class))).thenReturn(page);
        
        var result = auditService.getAllAuditLogs(PageRequest.of(0, 10));
        
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAuditLogsByAction() {
        when(auditLogRepository.findByActionAndEntityNameOrderByPerformedAtDesc("CREATE", "Club"))
            .thenReturn(List.of(auditLog));
        
        List<AuditLog> result = auditService.getAuditLogsByAction("Club", "CREATE");
        
        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
    }
}