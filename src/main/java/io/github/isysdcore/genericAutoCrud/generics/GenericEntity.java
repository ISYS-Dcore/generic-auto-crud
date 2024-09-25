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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *

 */

/**
 * @author domingos.fernando
 * @param <K> The Datatype for field ID, must be Long for numerical Ids or UUID for uuid ids,
 *          its will be mapped as Primary key in the database table.
 */
@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = {"deleted", "deletedAt", "updatedAt", "cratedAt"}, allowSetters = true)
public class GenericEntity<K> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private K id;
    @NotNull
    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date cratedAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    private Date deletedAt;
    private Boolean deleted = false;
    private UUID updatedBy;
}
