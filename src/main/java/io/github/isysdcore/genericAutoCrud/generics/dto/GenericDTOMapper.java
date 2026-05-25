package io.github.isysdcore.genericAutoCrud.generics.dto;

import java.util.List;

/**
 * Generic mapper contract for converting between entities and Data Transfer
 * Objects (DTOs).
 *
 * <p>Implementations are responsible for transforming domain entities into
 * DTOs and vice versa. This abstraction helps separate persistence models
 * from data exposed through APIs or other application layers.</p>
 *
 * @param <DTO> the Data Transfer Object type
 * @param <ENTITY> the entity type
 */
public interface GenericDTOMapper<DTO, ENTITY> {

    DTO toDto(ENTITY entity);

    ENTITY toEntity(DTO dto);

    List<DTO> toDtoList(List<ENTITY> entities);

    List<ENTITY> toEntityList(List<DTO> dtos);
}
