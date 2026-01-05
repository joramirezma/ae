package com.riwi.assesment.infrastructure.adapter.out.audit;

import com.riwi.assesment.domain.port.out.AuditLogPort;
import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import com.riwi.assesment.infrastructure.adapter.out.persistence.repository.JpaAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter implementing AuditLogPort.
 * Persists audit log entries to the database.
 */
@Component
public class AuditLogAdapter implements AuditLogPort {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAdapter.class);
    
    private final JpaAuditLogRepository auditLogRepository;

    public AuditLogAdapter(JpaAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void register(String action, UUID entityId) {
        UUID userId = getCurrentUserId();
        
        AuditLogEntity auditLog = new AuditLogEntity(action, entityId, userId);
        auditLogRepository.save(auditLog);
        
        logger.info("Audit log registered: action={}, entityId={}, userId={}", 
                action, entityId, userId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        if (authentication != null && authentication.getName() != null) {
            try {
                return UUID.fromString(authentication.getName());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
