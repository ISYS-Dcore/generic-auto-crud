/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isysdcore.genericAutoCrud.generics;

import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 *
 * @author domingos.fernando
 * @param <T>
 */
public interface GenericRestService<T, K> {

    public T save(T newEntity);

    public Optional<T> findById(K id);

    public T update(K id, T newEntity);

    public Page<T> findAll(String queryToSearch, int page, int size, int sort);

    public long count(String condToCount);

    public T delete(K id);

}
