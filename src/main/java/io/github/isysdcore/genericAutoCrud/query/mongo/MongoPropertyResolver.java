package io.github.isysdcore.genericAutoCrud.query.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

/// @author domingos.fernando
/// This class is responsible for resolving the type of a property in a MongoDB entity
/// given the entity class and the property path. It uses the MongoMappingContext
@Component
@RequiredArgsConstructor
public class MongoPropertyResolver {

    private final MongoMappingContext mappingContext;

    /*
     * Resolves the type of property in a MongoDB entity given the entity class and the property path.
     * @param entityClass   The class of the entity.
     * @param propertyPath  The path to the property, e.g., "address.street".
     * @return An Optional containing the Class type of the
     */

    public Optional<Class<?>> resolvePropertyType(Class<?> entityClass, String propertyPath) {
        MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(entityClass);
        if (entity == null) return Optional.empty();

        MongoPersistentProperty property = null;
        for (String part : propertyPath.split("\\.")) {
            if (entity == null) return Optional.empty();
            property = entity.getPersistentProperty(part);
            if (property == null) return Optional.empty();
            entity = mappingContext.getPersistentEntity(property.getActualType());
        }

        return Optional.ofNullable(property != null ? property.getActualType() : null);
    }
}
