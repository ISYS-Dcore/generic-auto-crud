package io.github.isysdcore.genericAutoCrud.audit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface AuditRepository extends CrudRepository<AuditLog, Long> {
}
