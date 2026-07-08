package io.github.isysdcore.genericAutoCrud.generics;

import org.springframework.data.domain.Page;

public interface GenericRestService <ENTITY, ID> {
     ENTITY save(ENTITY newEntity);
     ENTITY update(ID id, ENTITY updatedEntity);
     ENTITY update(ID id, ENTITY newEntity, ID updatedBy);
     ENTITY findById(ID id);
     Page<ENTITY> findAll(int page, int size, int sort);
     Page<ENTITY> findAll(String query, int page, int size, int sort);
     long count(String condToCount);
     ENTITY delete(ID id);
     ENTITY delete(ID id, ID deletedBy);
}
