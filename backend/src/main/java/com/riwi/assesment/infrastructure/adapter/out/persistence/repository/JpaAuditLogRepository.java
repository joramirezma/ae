package com.riwi.assesment.infrastructure.adapter.out.persistence.repository;

import com.riwi.assesment.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository for AuditLogEntity.
 */
@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}
