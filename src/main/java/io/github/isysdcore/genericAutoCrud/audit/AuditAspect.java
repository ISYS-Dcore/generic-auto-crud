package io.github.isysdcore.genericAutoCrud.audit;

import io.github.isysdcore.genericAutoCrud.audit.annotation.Auditable;
import io.github.isysdcore.genericAutoCrud.utils.AuditContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning("@annotation(auditable)")
    public void afterAuditedMethod(JoinPoint joinPoint, Auditable auditable) {
        String actor = AuditContext.getCurrentActor();
        String action = auditable.action();
        String step = auditable.step();
        String entity = auditable.entity();
        String entityId = auditable.entityId();

        String details = "Method: " + joinPoint.getSignature().toShortString();

        if(step.isBlank()) auditService.log(actor, action, entity, entityId, details);
        else auditService.log(actor, action, step, entity, entityId, details);
    }
}

