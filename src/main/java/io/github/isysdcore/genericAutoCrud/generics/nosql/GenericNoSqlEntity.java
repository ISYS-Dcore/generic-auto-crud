/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.isysdcore.genericAutoCrud.generics.nosql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Base abstract entity representing a database record with common audit fields.
 * By Default NoSql database ID will be of type String.
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
 *
 * @author domingos.fernando
 * @since v0.0.1
 */
@Getter
@Setter
@JsonIgnoreProperties(value = {"deleted", "deletedAt", "updatedAt", "createdAt", "updatedBy", "deletedBy"}, allowSetters = true)
public abstract class GenericNoSqlEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String resourceRef = UUID.randomUUID().toString();
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private Instant deletedAt;
    private Boolean deleted = false;
    private String updatedBy;
    private String deletedBy;

    /**
     * Programmatic fallback lifecycle hook for NoSQL engines that do not natively
     * support Spring Data Auditing listeners or auto-generated string keys.
     * Should be called explicitly in your generic service/repository layer before saving.
     */
    public void prepareForSave() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        this.updatedAt = Instant.now();

        // Dynamic UUID fallback string assignment if ID is null and ID type is String
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}
