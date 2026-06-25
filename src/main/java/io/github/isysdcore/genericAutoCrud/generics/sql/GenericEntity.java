/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.isysdcore.genericAutoCrud.generics.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Base abstract entity representing a database record with common audit fields.
 *
 * <p>This class defines shared persistence properties used across all entities,
 * including identifiers and audit metadata such as creation, update, and
 * deletion tracking.</p>
 *
 * <p>It is intended to be extended by all domain entities to ensure consistent
 * handling of identity and auditing information across the persistence layer.</p>
 *
 * <p>Typical fields include:</p>
 * <ul>
 *   <li>id</li>
 *   <li>createdAt</li>
 *   <li>updatedAt</li>
 *   <li>deletedAt</li>
 *   <li>deleted</li>
 *   <li>updatedBy</li>
 *   <li>deletedBy</li>
 * </ul>
 *
 * @param <ID> the identifier type used as the primary key. Common types are
 *             {@link java.lang.Long} for numeric IDs or {@link java.util.UUID}
 *             for UUID-based identifiers
 *
 * @author domingos.fernando
 * @since v0.0.1
 */
@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = {"deleted", "deletedAt", "updatedAt", "createdAt", "updatedBy", "deletedBy"}, allowSetters = true)
public abstract class GenericEntity<ID extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private ID id;

    @NotNull
    @Column(name = "resource_ref", nullable = false, unique = true, updatable = false)
    private String resourceRef = UUID.randomUUID().toString();
    @NotNull
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    private Instant deletedAt;
    private Boolean deleted = false;
    private ID updatedBy;
    private ID deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
