/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics;

import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 *
 * @author domingos.fernando
 * @param <T>
 */
public interface GenericRestService<T, K> {
    /**
     *
     * @param newEntity The new entity registry of type T to store in database
     * @return A database saved entity of type T
     */
    public T save(T newEntity);
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @return An optional object of type T
     */
    public Optional<T> findById(K id);
    /**
     *
     * @param id The unique main primary key that identify the database entity
     * @param newEntity The new entity registry of type T that will be used to update the old entity registry
     * @return Updated registry of type T
     */
    public T update(K id, T newEntity);
    /**
     *
     * @param queryToSearch Query to search in database follow the syntax "fieldName==value"
     * @param page The page counter start by 0
     * @param size The amount of items per page
     * @param sort The order of result 1 for ASC and -1 for DESC, default 0
     * @return Pageable object of type T
     */
    public Page<T> findAll(String queryToSearch, int page, int size, int sort);
    /**
     *
     * @param condToCount Query to use as a condition to count follow the syntax "fieldName==value"
     * @return The amount of entities of type T that match de condition
     */
    public long count(String condToCount);
    /**
     *
     * @param id The unique main primary key that identify the database registry entity
     * @return The entity founded in database or not found exception
     */
    public T delete(K id);

}
