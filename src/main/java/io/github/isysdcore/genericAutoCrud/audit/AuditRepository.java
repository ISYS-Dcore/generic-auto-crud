package io.github.isysdcore.genericAutoCrud.audit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Audit repository for saving audit logs to the database.
 * It extends CrudRepository to provide basic CRUD operations
 * for the AuditLog entity. This repository can be used by the AuditAspect to persist audit entries whenever
 * an auditable action is performed in the application.
 */
public interface AuditRepository extends CrudRepository<AuditLog, Long> {
}
