package io.github.isysdcore.genericAutoCrud.generics.dto;


import java.io.Serializable;

/**
 * Base contract for Data Transfer Objects (DTOs) that expose an identifier.
 *
 * <p>Implementing DTOs must provide accessors for the entity identifier,
 * allowing generic frameworks and services to work with DTO instances
 * independently of the identifier type.</p>
 *
 * @param <ID> the identifier type, which must implement {@link Serializable}
 */
public  interface GenericDto<ID extends Serializable> extends Serializable {
    ID getId();
    void setId(ID id);
}
