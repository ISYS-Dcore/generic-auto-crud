/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.generics;

import io.github.isysdcore.genericAutoCrud.query.CustomRsqlVisitor;
import io.github.isysdcore.genericAutoCrud.utils.DefaultSearchParameters;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domingos.fernando
 * @param <T> The Entity class that represent the database entity
 * @param <K> The Class type that represent the id field datatype of entity of type T
 * @param <R> The generic Repository modified by entity and id datatype injected
 */
public abstract class GenericRestServiceAbstract<T, R extends GenericRepository<T,K>, K>
        implements GenericRestService<T, K> {

    @Autowired
    public R repository;

    @Override
    public T save(T newEntity) {
       try{
           Field fdCreatedAt = newEntity.getClass().getSuperclass().getDeclaredField("cratedAt");
           fdCreatedAt.setAccessible(true);
           fdCreatedAt.set(newEntity, Calendar.getInstance().getTime());
           return repository.save(newEntity);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<T> findById(K id) {
        return repository.findById(id);
    }

    public Page<T> findAll(int page, int size, int sort) {
        String defaultQuery = "deleted==false";
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec, DefaultSearchParameters.preparePages(page, size, sort));
    }

    @Override
    public Page<T> findAll(String query, int page, int size, int sort) {
        String defaultQuery = "deleted==false;" + query;
        Node rootNode = new RSQLParser().parse(defaultQuery);
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.findAll(spec, DefaultSearchParameters.preparePages(page, size, sort));
    }

    @Override
    public long count(String condToCount) {
        condToCount = "deleted==FALSE;" + condToCount;
        Node rootNode = new RSQLParser().parse(condToCount);
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<>());
        return repository.count(spec);
    }

    @Override
    public T update(K id, T newEntity) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {
                        List<Field> fieldList = Arrays.asList(oldEntity.getClass().getFields());
                        fieldList.forEach(oldField -> {
                            oldField.setAccessible(true);
                            try {
                                Field fd = newEntity.getClass().getDeclaredField(oldField.getName());
                                fd.setAccessible(true);
                                if(fd.get(newEntity) == null)
                                {
                                    fd.set(newEntity, oldField.get(oldEntity));
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, e);
                            }
                        });
                        Field fdUpdatedAt = newEntity.getClass().getSuperclass().getDeclaredField("updatedAt");
                        fdUpdatedAt.setAccessible(true);
                        fdUpdatedAt.set(newEntity, Calendar.getInstance().getTime());
                        Field idUpdatedAt = newEntity.getClass().getSuperclass().getDeclaredField("id");
                        idUpdatedAt.setAccessible(true);
                        idUpdatedAt.set(newEntity, id);
                    } catch (SecurityException | IllegalArgumentException | NoSuchFieldException |
                             IllegalAccessException ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(newEntity);
                }) //
                .orElseThrow( () -> new ResourceNotFoundException(id.toString()));
    }

    @Override
    public T delete(K id) {
        return repository.findById(id) //
                .map(oldEntity -> {
                    try {

                        Field fdDeletedAt = oldEntity.getClass().getSuperclass().getDeclaredField("deletedAt");
                        fdDeletedAt.setAccessible(true);
                        fdDeletedAt.set(oldEntity, Calendar.getInstance().getTime());

                        Field fdIsDeleted = oldEntity.getClass().getSuperclass().getDeclaredField("deleted");
                        fdIsDeleted.setAccessible(true);
                        fdIsDeleted.set(oldEntity, Boolean.TRUE);

                    } catch (NoSuchFieldException | IllegalAccessException ex) {
                        Logger.getLogger(GenericRestServiceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return repository.save(oldEntity);
                }) //
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
    }

}
