package io.github.isysdcore.genericAutoCrud.test.generics;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isysdcore.genericAutoCrud.generics.GenericBaseEntity;
import io.github.isysdcore.genericAutoCrud.test.generics.model.TestProperties;
import io.github.isysdcore.genericAutoCrud.test.utils.UtilServiceTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Abstract integration test for entities extending {@link GenericBaseEntity}.
 * Concrete subclasses only need to pass the endpoint path and a template entity instance.
 * <p>
 * All CRUD tests are provided. Authentication can be added by overriding
 * {@link #authenticationManagement()} and setting the token in {@link #testProperties}.
 * <p>
 * For record‑based DTOs, use {@link GenericRestIntegrationDtoTests} instead.
 *
 * @param <T> the entity type
 * @param <K> the entity's key type (unused, kept for compatibility)
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public abstract class GenericRestIntegrationTestsImpl<T extends GenericBaseEntity<K>, K>
        implements RestIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UtilServiceTests utilsServices;

    protected final String endpoint;
    protected final T entityTemplate;
    protected T entityInstance;
    protected String createdEntityId;
    protected TestProperties testProperties;

    /**
     * @param endpoint     the path relative to the application's base URL,
     *                     e.g. "/v1/api/subscribers"
     * @param entityTemplate a fresh, empty entity instance used as a template
     */
    protected GenericRestIntegrationTestsImpl(String endpoint, T entityTemplate) {
        this.endpoint = endpoint;
        this.entityTemplate = entityTemplate;
        this.testProperties = TestProperties.builder()
                .resourceUrl(endpoint)
                .auth(false)
                .build();
    }

    @BeforeEach
    public void setUp() throws Exception {
        authenticationManagement();
        prepareNewEntity();
    }

    /**
     * Fills {@link #entityInstance} with random valid data using {@link UtilServiceTests}.
     * Generates a UUID for the entity's ID.
     */
    public void prepareNewEntity() {
        try {
            entityInstance = (T) entityTemplate.getClass().getDeclaredConstructor().newInstance();
            for (Field field : entityTemplate.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if ("id".equals(field.getName())) {
                    continue;
                }
                try {
                    UtilServiceTests.setValueByClassType(field, entityInstance);
                } catch (IllegalAccessException e) {
                    log.error("Failed to set field {}", field.getName(), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate entity", e);
        }
    }

    /**
     * Override to perform authentication (e.g. login and obtain a JWT token)
     * and store it in {@link #testProperties}. Example:
     * <pre>{@code
     *   testProperties.setAuth(true);
     *   testProperties.setAuthHeaderName("Authorization");
     *   testProperties.setTokenType("Bearer");
     *   testProperties.setAuthToken(acquiredToken);
     * }</pre>
     */
    public void authenticationManagement() throws Exception {
        log.warn("No authentication management implemented. Override this method to set up authentication if required.");
    }

    /**
     * Store a given number of pre‑populated entities in the database.
     * By default this does nothing; override to call your service layer,
     * e.g. {@code entityService.saveAll(listOfEntities)}.
     */
    public void storeEntitiesInDb(int quantity) {
        // Override in concrete test class if needed
        log.warn("storeEntitiesInDb not implemented. Override this method to persist entities in the database for testing.");
    }

    // -----------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------

    private void addAuthIfRequired(MockHttpServletRequestBuilder request) {
        if (testProperties.isAuth() &&
                testProperties.getAuthHeaderName() != null &&
                testProperties.getTokenType() != null &&
                testProperties.getAuthToken() != null) {
            request.header(testProperties.getAuthHeaderName(),
                    testProperties.getTokenType() + " " + testProperties.getAuthToken());
        }
    }

    // =================================================================
    // REST Integration Tests – fully implemented
    // =================================================================

    @Override
    @Test
    public void shouldCreateEntity() throws Exception {
        var request = post(testProperties.getResourceUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entityInstance));
        addAuthIfRequired(request);

        String response = mockMvc.perform(request)
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    Assertions.assertTrue(
                            status == HttpStatus.OK.value() ||
                                    status == HttpStatus.CREATED.value() ||
                                    status == HttpStatus.ACCEPTED.value(),
                            "Expected 200, 201 or 202 but got " + status);
                })
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        createdEntityId = objectMapper.readTree(response).get("id").asText();
    }

    @Override
    @Test
    public void shouldReturnEntity() throws Exception {
        ensureCreated();

        var request = get(testProperties.getResourceUrl() + "/{id}", createdEntityId)
                .contentType(MediaType.APPLICATION_JSON);
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdEntityId))
                .andExpect(jsonPath("$." + getFirstFieldName()).isNotEmpty())
                .andExpect(jsonPath("$." + getSecondFieldName()).isNotEmpty())
                .andDo(print());
    }

    @Override
    @Test
    public void shouldReturnListOfEntities() throws Exception {
        ensureCreated();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "10");
        params.add("sort", "0");

        var request = get(testProperties.getResourceUrl()).params(params)
                .contentType(MediaType.APPLICATION_JSON);
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(greaterThan(0)))
                .andDo(print());
    }

    @Override
    @Test
    public void shouldReturnListOfEntitiesWithFilter() throws Exception {
        ensureCreated();
        String fieldName = getFirstFieldName();
        Object fieldValue = getFieldValue(entityInstance, fieldName);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "10");
        params.add("sort", "0");
        params.add("query", fieldName + "==*" + fieldValue + "*");

        var request = get(testProperties.getResourceUrl() + "/search").params(params)
                .contentType(MediaType.APPLICATION_JSON);
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(greaterThan(0)))
                .andDo(print());
    }

    @Override
    @Test
    public void shouldReturnNoContentWhenFilter() throws Exception {
        String fieldName = getFirstFieldName();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "10");
        params.add("sort", "0");
        params.add("query", fieldName + "==NONEXISTENT");

        var request = get(testProperties.getResourceUrl() + "/search").params(params)
                .contentType(MediaType.APPLICATION_JSON);
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andDo(print());
    }

    @Override
    @Test
    public void shouldUpdateEntity() throws Exception {
        ensureCreated();
        T before = entityInstance;
        String firstFieldName = getFirstFieldName();
        Object oldFirst = getFieldValue(before, firstFieldName);
        String secondFieldName = getSecondFieldName();
        Object oldSecond = getFieldValue(before, secondFieldName);

        prepareNewEntity(); // randomise the entity

        var request = put(testProperties.getResourceUrl() + "/{id}", createdEntityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entityInstance));
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdEntityId))
                .andExpect(jsonPath("$." + firstFieldName).value(not(oldFirst)))
                .andExpect(jsonPath("$." + secondFieldName).value(not(oldSecond)))
                .andDo(print());
    }

    @Override
    @Test
    public void shouldDeleteEntity() throws Exception {
        ensureCreated();

        var request = delete(testProperties.getResourceUrl() + "/{id}", createdEntityId)
                .contentType(MediaType.APPLICATION_JSON);
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Override
    @Test
    public void shouldReturnNotFoundEntity() throws Exception {
        var request = get(testProperties.getResourceUrl() + "/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Override
    @Test
    public void shouldReturnNotFoundUpdateEntity() throws Exception {
        var request = put(testProperties.getResourceUrl() + "/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entityInstance));
        addAuthIfRequired(request);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // =================================================================
    // Reflection helpers (record‑safe)
    // =================================================================

    private void ensureCreated() throws Exception {
        if (createdEntityId == null) {
            shouldCreateEntity();
        }
    }

    private String getFirstFieldName() {
        Field[] fields = entityTemplate.getClass().getDeclaredFields();
        return fields.length > 0 ? fields[0].getName() : "id";
    }

    private String getSecondFieldName() {
        Field[] fields = entityTemplate.getClass().getDeclaredFields();
        return fields.length > 1 ? fields[1].getName() : "id";
    }

    /**
     * Retrieves a field value from an object, using the record's accessor method
     * if the object is a Java record, otherwise falling back to reflective field access.
     */
    private Object getFieldValue(Object obj, String fieldName) {
        try {
            if (obj.getClass().isRecord()) {
                Method accessor = obj.getClass().getMethod(fieldName);
                return accessor.invoke(obj);
            }
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get value of field '" + fieldName + "'", e);
        }
    }
}