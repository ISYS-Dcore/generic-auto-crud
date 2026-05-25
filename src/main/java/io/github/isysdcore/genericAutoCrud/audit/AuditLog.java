package io.github.isysdcore.genericAutoCrud.audit;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * Audit log data model representing an audit entry in the system. It captures details about actions performed,
 * including the actor, action type, step, target entity, and additional details.
 * This model can be persisted to an "audit_log" table in the database for tracking and auditing purposes.
 */
@Data
@Table(schema = "audit_log")
public class AuditLog {
    @Id
    private Long id;
    private String actor;            // e.g., username or system
    private String action;           // e.g., CREATE, UPDATE, DELETE, LOGIN, FILE_PROCESSED
    private String step;             // e.g., VALIDATE_DATA, VALIDATE_INTEGRITY
    private String entity;           // target entity name or module
    private String entityId;         // optional
    private String details;          // JSON or text
    private Instant timestamp = Instant.now();
    private String source;           // microservice or app name
    private String correlationId;    // trace ID
}
