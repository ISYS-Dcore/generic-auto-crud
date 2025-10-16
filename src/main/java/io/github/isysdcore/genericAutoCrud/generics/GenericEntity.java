/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.isysdcore.genericAutoCrud.generics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/// GenericEntity is an abstract class that represents a generic entity
/// in the database. It contains common fields such as id, createdAt,
/// updatedAt, deletedAt, deleted, updatedBy, and deletedBy.
/// This class is intended to be extended by other entity classes
/// @author domingos.fernando
/// @param <K> The Datatype for field ID, must be Long for numerical Ids or UUID for uuid ids,
///          its will be mapped as Primary key in the database table.
@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = {"deleted", "deletedAt", "updatedAt", "createdAt", "updatedBy", "deletedBy"}, allowSetters = true)
public abstract class GenericEntity<K> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private K id;
    @NotNull
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    private Instant deletedAt;
    private Boolean deleted = false;
    private K updatedBy;
    private K deletedBy;

}
