package io.github.isysdcore.genericAutoCrud.audit.annotation;

import java.lang.annotation.*;

/**
 * Interface to inject audit log on your project and trace the operations
 * that users or system execute
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String action();
    String step() default "";
    String entity() default "";
    String entityId() default "";
}