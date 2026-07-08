package io.github.isysdcore.genericAutoCrud.generics.dto;

import org.springframework.data.domain.Page;

public interface GenericRestServiceDto<DTO, ID> {
     DTO save(DTO newEntity);
     DTO findById(ID id);
     Page<DTO> findAll(int page, int size, int sort);
     Page<DTO> findAll(String query, int page, int size, int sort);
     long count(String condToCount);
     DTO update(ID id, DTO newEntity);
     DTO update(ID id, DTO newEntity, ID updatedBy);
     DTO delete(ID id);
     DTO delete(ID id, ID deletedBy);
}
