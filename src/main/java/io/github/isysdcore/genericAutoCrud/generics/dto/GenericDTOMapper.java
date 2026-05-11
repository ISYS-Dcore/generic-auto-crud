package io.github.isysdcore.genericAutoCrud.generics.dto;

import java.util.List;

public interface GenericDTOMapper<DTO, ENTITY> {

    DTO toDto(ENTITY entity);

    ENTITY toEntity(DTO dto);

    List<DTO> toDtoList(List<ENTITY> entities);

    List<ENTITY> toEntityList(List<DTO> dtos);
}
