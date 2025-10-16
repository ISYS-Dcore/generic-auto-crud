package io.github.isysdcore.genericAutoCrud.generics.cache;

import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;
import io.github.isysdcore.genericAutoCrud.generics.mongo.MongoGenericRestServiceAbstract;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Generic class to cache database entities in memory
/// @Warning Use this with caution because load too much information to memory can cause error and other issues
/// @param <K> Entity id data type
/// @param <T> Entity type
/// @param <S> The service that provide entity operations
/// @code Example:
///  GenericCache<String, MyEntity, MyService> cache =
///  new GenericCache<>(myService);
///  // Add multiple indexes
///  cache.addSecondaryIndex("byName", e -> e.getName());
///  cache.addSecondaryIndex("byNameAndType", e -> Arrays.asList(e.getName(), e.getType()));
///  // Now you can query in O(1)
///  List<MyEntity> list = cache.getByIndex("byNameAndType", Arrays.asList("John", "Admin"));
public class GenericMongoCache<K, T extends GenericEntity<K>, S extends MongoGenericRestServiceAbstract<T,?,?>> {

    private final S entityService;
    private Map<K, T> cacheEntities;
    private final Map<String, Map<Object, List<T>>> secondaryIndexes = new ConcurrentHashMap<>();
    private final Map<String, Function<T, Object>> indexExtractors = new HashMap<>();

    public GenericMongoCache(S entityService){
        this.entityService = entityService;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        List<T> allEntities = entityService.findAll(0, 100, 1).stream().toList();
        cacheEntities = allEntities.stream()
                .collect(Collectors.toMap(T::getId, Function.identity()));
        // Secondary indexes
        secondaryIndexes.clear();
        indexExtractors.forEach((name, extractor) -> {
            Map<Object, List<T>> indexMap = allEntities.stream()
                    .collect(Collectors.groupingBy(extractor, HashMap::new, Collectors.toList()));
            secondaryIndexes.put(name, indexMap);
        });
    }

    public T get(K key) {
        return cacheEntities.get(key);
    }

    public Map<K, T> getAll() {
        return Collections.unmodifiableMap(cacheEntities);
    }

    /**
     * Adds a secondary index to the cache.
     * @param name The name of the index.
     * @param keyExtractor A function that extracts the key for the index from an entity.
     */
    public void addSecondaryIndex(String name, Function<T, Object> keyExtractor) {
        indexExtractors.put(name, keyExtractor);
    }

    /**
     * Retrieves entities by a secondary index.
     * @param indexName The name of the index to query.
     * @param key The key to search for in the index.
     * @return A list of entities that match the key in the specified index.
     */
    public List<T> getByIndex(String indexName, Object key) {
        return secondaryIndexes
                .getOrDefault(indexName, Collections.emptyMap())
                .getOrDefault(key, Collections.emptyList());
    }

}
