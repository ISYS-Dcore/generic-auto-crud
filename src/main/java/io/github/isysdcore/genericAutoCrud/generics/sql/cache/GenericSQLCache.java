package io.github.isysdcore.genericAutoCrud.generics.sql.cache;

import io.github.isysdcore.genericAutoCrud.generics.GenericRestService;
import io.github.isysdcore.genericAutoCrud.generics.sql.GenericSqlEntity;
import jakarta.annotation.PostConstruct;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic in-memory cache for database entities.
 *
 * <p><strong>Warning:</strong> Use this class with caution. Loading large
 * amounts of data into memory may increase memory usage and can lead to
 * performance issues or {@link OutOfMemoryError} exceptions.</p>
 *
 * @param <ID> the entity identifier type
 * @param <ENTITY> the entity type being cached
 * @param <SERVICE> the service responsible for entity operations
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * GenericCache<String, MyEntity, MyService> cache =
 *         new GenericCache<>(myService);
 *
 * // Add multiple indexes
 * cache.addSecondaryIndex("byName", e -> e.getName());
 * cache.addSecondaryIndex(
 *         "byNameAndType",
 *         e -> Arrays.asList(e.getName(), e.getType())
 * );
 *
 * // Query entities using a secondary index
 * List<MyEntity> list =
 *         cache.getByIndex(
 *                 "byNameAndType",
 *                 Arrays.asList("John", "Admin")
 *         );
 * }</pre>
 *
 * @author domingos.fernando
 * @since 0.1.0
 */
public class GenericSQLCache<ID extends Serializable, ENTITY extends GenericSqlEntity<ID>, SERVICE extends GenericRestService<ENTITY,ID>> {

    private final SERVICE entityService;
    private Map<ID, ENTITY> cacheEntities;
    private final Map<String, Map<Object, List<ENTITY>>> secondaryIndexes = new ConcurrentHashMap<>();
    private final Map<String, Function<ENTITY, Object>> indexExtractors = new HashMap<>();

    public GenericSQLCache(SERVICE entityService){
        this.entityService = entityService;
    }

    /**
     * Post construct  initializer
     */
    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        List<ENTITY> allEntities = entityService.findAll(0, 100, 1).stream().toList();
        cacheEntities = allEntities.stream()
                .collect(Collectors.toMap(ENTITY::getId, Function.identity()));
        // Secondary indexes
        secondaryIndexes.clear();
        indexExtractors.forEach((name, extractor) -> {
            Map<Object, List<ENTITY>> indexMap = allEntities.stream()
                    .collect(Collectors.groupingBy(extractor, HashMap::new, Collectors.toList()));
            secondaryIndexes.put(name, indexMap);
        });
    }

    /**
     * Get method to load Instance by key
     * @param key The identifier of instance
     * @return an instance of type ENTITY
     */
    public ENTITY get(ID key) {
        return cacheEntities.get(key);
    }

    /**
     * Load all instances off type ENTITY
     * @return Map of instances of type ENTITY where key is the identifier
     */
    public Map<ID, ENTITY> getAll() {
        return Collections.unmodifiableMap(cacheEntities);
    }

    /**
     * Adds a secondary index to the cache.
     * @param name The name of the index.
     * @param keyExtractor A function that extracts the key for the index from an entity.
     */
    public void addSecondaryIndex(String name, Function<ENTITY, Object> keyExtractor) {
        indexExtractors.put(name, keyExtractor);
    }

    /**
     * Retrieves entities by a secondary index.
     * @param indexName The name of the index to query.
     * @param key The key to search for in the index.
     * @return A list of entities that match the key in the specified index.
     */
    public List<ENTITY> getByIndex(String indexName, Object key) {
        return secondaryIndexes
                .getOrDefault(indexName, Collections.emptyMap())
                .getOrDefault(key, Collections.emptyList());
    }

}
