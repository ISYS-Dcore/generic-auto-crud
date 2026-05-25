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
 * Generic REST controller contract defining standard CRUD operations for a Spring Boot application.
 *
 * <p>This interface provides a reusable abstraction for building RESTful APIs across different
 * entity types, enforcing a consistent structure for Create, Read, Update, and Delete operations.</p>
 *
 * <p>It is intended to be implemented or extended by concrete controllers in order to reduce
 * boilerplate code and standardize API behavior across the application.</p>
 *
 * @param <ENTITY> the entity type representing the database model
 * @param <ID> the identifier type of the entity
 *
 * @author domingos.fernando
 */
public interface GenericRestController<ENTITY, ID> {

    /**
     *
     * @return All Object of type ENTITY
     */
    public Page<ENTITY> findAll(int page, int size, int sort);

    /**
     *
     * @param page - Is de number of the page that we requested.
     * @param size - Is the size of the items per page.
     * @param sort - Is the sort order 1 for ascendent and -1 for descendent.
     * @param query - Is the query that we are searched for.
     * @return a paginated list of objects from type.
     */
    public Page<ENTITY> findByQuery(int page, int size, int sort, String query);

    /**
     *
     * @param id - Primary key of object that we searched
     * @return - The entity we searched it
     */
    public ResponseEntity<?> findById(@PathVariable ID id);

    /**
     *
     * @param newEntity - New entity to persist on database
     * @return - The created entity
     */
    public ResponseEntity<?> create(@RequestBody ENTITY newEntity);

    /**
     *
     * @param id - Primary key for entity that we will update.
     * @param newEntity - Entity with de new data.
     * @return - The updated Entity
     */
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody ENTITY newEntity);

    /**
     *
     * @param id - Primary key for object that we will deleted
     * @return - The deleted entity.
     */
    public ResponseEntity<?> delete(@PathVariable ID id);

}
