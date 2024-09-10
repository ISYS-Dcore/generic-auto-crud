/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author domingos.fernando
 * @param <T> The Entity class that represent the database entity
 * @param <K> The Class type that represent the id field datatype of entity of type T
 */
public interface GenericRestController<T, K> {

    /**
     *
     * @return All Object of type T
     */
//    public CollectionModel<EntityModel<Object>> findAll();
    public Page<T> findAll(int page, int size, int sort);

    /**
     *
     * @param page - Is de number of the page that we requested.
     * @param size - Is the size of the items per page.
     * @param sort - Is the sort order 1 for ascendent and -1 for descendent.
     * @param query - Is the query that we are searched for.
     * @return a paginated list of objects from type.
     */
    public Page<T> findByQuery(int page, int size, int sort, String query);

    /**
     *
     * @param id - Primary key of object that we searched
     * @return - The entity we searched it
     */
    public ResponseEntity<?> findById(@PathVariable K id);

    /**
     *
     * @param newEntity - New entity to persist on database
     * @return - The created entity
     */
    public ResponseEntity<?> create(@RequestBody T newEntity);

    /**
     *
     * @param id - Primary key for entity that we will update.
     * @param newEntity - Entity with de new data.
     * @return - The updated Entity
     */
    public ResponseEntity<?> update(@PathVariable K id, @RequestBody T newEntity);

    /**
     *
     * @param id - Primary key for object that we will deleted
     * @return - The deleted entity.
     */
    public ResponseEntity<?> delete(@PathVariable K id);

}
