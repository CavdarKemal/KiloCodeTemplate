package com.verein.repository;

import com.verein.entity.AuditLog;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuditLogRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        AuditLog log1 = AuditLog.builder()
                .entityName("Club")
                .entityId(1L)
                .action("CREATE")
                .newValue("{\"name\":\"FC Berlin\"}")
                .performedBy("admin")
                .performedAt(LocalDateTime.now())
                .build();
        entityManager.persist(log1);

        AuditLog log2 = AuditLog.builder()
                .entityName("Club")
                .entityId(1L)
                .action("UPDATE")
                .oldValue("{\"name\":\"FC Berlin\"}")
                .newValue("{\"name\":\"FC Hamburg\"}")
                .performedBy("admin")
                .performedAt(LocalDateTime.now().plusHours(1))
                .build();
        entityManager.persist(log2);

        AuditLog log3 = AuditLog.builder()
                .entityName("Member")
                .entityId(1L)
                .action("CREATE")
                .newValue("{\"firstName\":\"Max\"}")
                .performedBy("admin")
                .performedAt(LocalDateTime.now())
                .build();
        entityManager.persist(log3);

        entityManager.flush();
    }

    @Test
    void testFindByEntityNameAndEntityIdOrderByPerformedAtDesc() {
        List<AuditLog> result = auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc("Club", 1L);
        
        assertEquals(2, result.size());
        assertEquals("UPDATE", result.get(0).getAction());
    }

    @Test
    void testFindByPerformedByOrderByPerformedAtDesc() {
        List<AuditLog> result = auditLogRepository.findByPerformedByOrderByPerformedAtDesc("admin");
        
        assertEquals(3, result.size());
    }

    @Test
    void testFindByEntityNameOrderByPerformedAtDesc() {
        List<AuditLog> result = auditLogRepository.findByEntityNameOrderByPerformedAtDesc("Club");
        
        assertEquals(2, result.size());
    }

    @Test
    void testFindByActionAndEntityNameOrderByPerformedAtDesc() {
        List<AuditLog> result = auditLogRepository.findByActionAndEntityNameOrderByPerformedAtDesc("CREATE", "Club");
        
        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
    }
}