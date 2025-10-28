package io.github.isysdcore.genericAutoCrud.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 *
 * Auditlog Automátic spring security usage
 * /@Component
 * public class AuditContextFilter extends OncePerRequestFilter {
 *     //@Override
 *     protected void doFilterInternal(HttpServletRequest request,
 *                                     HttpServletResponse response,
 *                                     FilterChain filterChain) throws ServletException, IOException {
 *         var auth = SecurityContextHolder.getContext().getAuthentication();
 *         if (auth != null and auth.isAuthenticated()) {
 *             AuditContext.setCurrentActor(auth.getName());
 *         }
 *         try {
 *             filterChain.doFilter(request, response);
 *         } finally {
 *             AuditContext.clear();
 *         }
 *     }
 * }
 *
 * Audit log Manual usage
 * /@Service
 * /@RequiredArgsConstructor
 * public class FileProcessingService {
 *     private final AuditService auditService;
 *
 *     public void processFile(String file) {
 *         // business logic
 *         auditService.log("system", "FILE_PROCESSED", "File", file, "File processed successfully");
 *     }
 * }
 *
 * OR with Notation
 * /@Service
 * public class UserService {
 *     //@Auditable(action = "USER_CREATED", entity = "User")
 *     public void createUser(User user) {
 *         // ...
 *     }
 * }
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    @Autowired
    private AuditRepository repo;

    @Async
    public void log(String actor, String action, String entity, String entityId, String details) {
        AuditLog entry = new AuditLog();
        entry.setActor(actor != null ? actor : "SYSTEM");
        entry.setAction(action);
        entry.setEntity(entity);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        entry.setTimestamp(Instant.now());
        entry.setSource(System.getenv().getOrDefault("APP_NAME", "unknown"));
        repo.save(entry);

        log.info("AUDIT_LOG: actor={} action={} entity={} entityId={} details={}",
                actor, action, entity, entityId, details);
    }

    @Async
    public void log(String actor, String action, String step, String entity, String entityId, String details) {
        AuditLog entry = new AuditLog();
        entry.setActor(actor != null ? actor : "SYSTEM");
        entry.setAction(action);
        entry.setStep(step);
        entry.setEntity(entity);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        entry.setTimestamp(Instant.now());
        entry.setSource(System.getenv().getOrDefault("APP_NAME", "unknown"));
        repo.save(entry);

        log.info("AUDIT_LOG: actor={} action={} step={} entity={} entityId={} details={}",
                actor, action, step, entity, entityId, details);
    }
}

