package io.github.isysdcore.genericAutoCrud.generics;

import java.time.Instant;

public interface GenericBaseEntity<ID> {
    ID getId();
    void setId(ID primaryKey);
    Instant getCreatedAt();
    void setCreatedAt(Instant now);
    String getResourceRef();
    void setResourceRef(String resourceRef);
    void setUpdatedAt(Instant now);
    Instant getUpdatedAt();
    void setDeletedAt(Instant now);
    Instant getDeletedAt();
    Boolean getDeleted();
    void setDeleted(Boolean deleted);
}
