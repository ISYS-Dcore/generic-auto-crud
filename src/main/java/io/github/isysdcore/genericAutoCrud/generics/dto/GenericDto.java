package io.github.isysdcore.genericAutoCrud.generics.dto;

import java.io.Serializable;

public interface GenericDto<ID extends Serializable> extends Serializable {
    ID getId();
    void setId(ID id);
}
