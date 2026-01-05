package com.riwi.assesment.domain.port.out;

import java.util.UUID;

/**
 * Output port for audit logging operations.
 * This interface defines the contract for registering audit events.
 */
public interface AuditLogPort {

    /**
     * Registers an audit log entry for a specific action on an entity.
     * @param action the action performed (e.g., "CREATE_PROJECT", "COMPLETE_TASK")
     * @param entityId the ID of the affected entity
     */
    void register(String action, UUID entityId);
}
