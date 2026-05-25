package io.github.isysdcore.genericAutoCrud.generics.cache;

import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import io.github.isysdcore.genericAutoCrud.generics.mongo.MongoGenericRestServiceAbstract;
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
 * <p>
 * This cache loads entities from the data source and stores them in memory
 * to provide fast lookups. In addition to primary key access, custom
 * secondary indexes can be created to support constant-time lookups by
 * arbitrary entity attributes.
 * </p>
 *
 * <p><strong>Warning:</strong> Use this class with caution. Caching large
 * datasets may significantly increase memory consumption and can lead to
 * performance degradation or {@link OutOfMemoryError} exceptions.
 * Consider using it only when the dataset size is predictable and fits
 * comfortably in available memory.
 * </p>
 *
 * @param <ID> the entity identifier type
 * @param <ENTITY> the entity type being cached
 * @param <SERVICE> the service type responsible for entity operations
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * GenericCache<String, MyEntity, MyService> cache =
 *         new GenericCache<>(myService);
 *
 * // Add secondary indexes
 * cache.addSecondaryIndex("byName", MyEntity::getName);
 *
 * cache.addSecondaryIndex(
 *         "byNameAndType",
 *         e -> Arrays.asList(e.getName(), e.getType())
 * );
 *
 * // Query entities using a secondary index
 * List<MyEntity> users =
 *         cache.getByIndex(
 *                 "byNameAndType",
 *                 Arrays.asList("John", "Admin")
 *         );
 * }</pre>
 *
 * <p>
 * Secondary indexes map a key derived from an entity to one or more cached
 * entities. Once an index is created, lookups using that index are typically
 * performed in constant time.
 * </p>
 *
 * @author Domingos Fernando
 * @since 1.0
 */
public class GenericMongoCache<ID extends Serializable, ENTITY extends GenericEntity<ID>, SERVICE extends MongoGenericRestServiceAbstract<ENTITY,?,?>> {

    private final SERVICE entityService;
    private Map<ID, ENTITY> cacheEntities;
    private final Map<String, Map<Object, List<ENTITY>>> secondaryIndexes = new ConcurrentHashMap<>();
    private final Map<String, Function<ENTITY, Object>> indexExtractors = new HashMap<>();

    public GenericMongoCache(SERVICE entityService){
        this.entityService = entityService;
    }

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

    public ENTITY get(ID key) {
        return cacheEntities.get(key);
    }

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
