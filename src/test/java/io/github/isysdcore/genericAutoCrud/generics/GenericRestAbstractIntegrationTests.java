package io.github.isysdcore.genericAutoCrud.generics;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isysdcore.genericAutoCrud.generics.model.TestProperties;
import io.github.isysdcore.genericAutoCrud.generics.sql.GenericRestServiceAbstract;
import io.github.isysdcore.genericAutoCrud.utils.Constants;
import io.github.isysdcore.genericAutoCrud.utils.UtilServiceTests;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author domingos.fernando
 * @created 26/12/2024 - 16:52
 * @project smsg
 */
@Slf4j
@Getter
public abstract class GenericRestAbstractIntegrationTests<T extends GenericEntity<?>, S extends GenericRestServiceAbstract<?,?, ?>> {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private S entityService;
    @Autowired
    private UtilServiceTests utilService;

    private T entity;
    private final TestProperties testProperties;

    public GenericRestAbstractIntegrationTests(T newEntity, TestProperties testProperties){
        this.entity = newEntity;
        this.testProperties = testProperties;
    }


    @BeforeEach
    public void setup() throws Exception {
        authenticationManagement();
        this.prepareNewEntity(getEntity());
    }

    /**
     * If your application need authentication, override this method,
     * to create a new user a make login storing Json Web Token in 'bearerJwToken'
     * variable and other methods will detect it and use in all other operations
     * If your operations don't need authentication just leave this method empty
     */
    public abstract void authenticationManagement() throws Exception;

//    public void shouldAuthenticateUser() throws Exception{
//
//        Map<String, Object> loginRequest = new HashMap<>();
//        loginRequest.put("username", this.userName);
//        loginRequest.put("password", this.userPassword);
//        loginRequest.put("rememberMe", false);
//
//        MvcResult loginResult = getMockMvc().perform(post(loginEndpoint)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(getObjectMapper().writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        String json = loginResult.getResponse().getContentAsString();
//        bearerJwToken = getObjectMapper().readValue(json, JwtResponse.class).token();
//    }

    public void prepareNewEntity(T newEntity) {
        this.entity = (T) utilService.prepareSimpleEntity(newEntity);
    }

    public void storeEntitiesInDb(Integer quantity) {
        assert quantity > 0;
        for (int i = 0; i < quantity; i++) {
            prepareNewEntity(entity);
//            this.entity = entityService.save(entity);
        }
    }

    @Test
    public void shouldCreateEntity() throws Exception{
        var request = post(testProperties.getResourceUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(getEntity()));
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    Assertions.assertTrue(status == HttpStatus.OK.value() || status == HttpStatus.ACCEPTED.value()
                            || status == HttpStatus.CREATED.value(), "Expected 200 OK, 201 Created or 202 Accepted but got " + status);
                })
                .andDo(print());
    }

    private boolean setAuthentication(MockHttpServletRequestBuilder request) {
        if(testProperties.getAuthHeaderName().isBlank() ||
                testProperties.getTokenType().isBlank() ||
                testProperties.getAuthToken().isBlank()){
            log.error("Authentication is enabled on properties={} but authToken is empty", testProperties);
            return false;
        }
        request.header(testProperties.getAuthHeaderName(), testProperties.getTokenType() + " "
                + testProperties.getAuthToken());
        return true;
    }

    @Test
    public void shouldReturnEntityByGivenId() throws Exception {
        storeEntitiesInDb(1);
        Field[] fields = getUtilService().collectEntityFields(getEntity());
        var request = get(testProperties.getResourceUrl() + Constants.RESOURCE_BY_ID, getEntity().getId())
                .contentType(MediaType.APPLICATION_JSON);
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getEntity().getId().toString()))
                .andExpect(jsonPath("$." + fields[0].getName()).isNotEmpty())
                .andExpect(jsonPath("$." + fields[1].getName()).isNotEmpty())
                .andDo(print());
    }

    @Test
    public void shouldReturnListOfEntities() throws Exception {
        storeEntitiesInDb(2);
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add(Constants.PAGE, "0");
        paramsMap.add(Constants.SIZE, "10");
        paramsMap.add(Constants.SORT, "0");
        var request = get(testProperties.getResourceUrl()).params(paramsMap)
                .contentType(MediaType.APPLICATION_JSON);
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)))
                .andDo(print());

    }

    @Test
    public void shouldReturnListOfEntitiesWithFilter() throws Exception {
        storeEntitiesInDb(2);
        Field[] fields = getUtilService().collectEntityFields(getEntity());
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add(Constants.PAGE, "0");
        paramsMap.add(Constants.SIZE, "10");
        paramsMap.add(Constants.SORT, "0");
        paramsMap.add(Constants.QUERY,  fields[0].getName() + "==" + fields[0].get(getEntity()));
        var request = get(testProperties.getResourceUrl() + Constants.RESOURCE_SEARCH).params(paramsMap)
                .contentType(MediaType.APPLICATION_JSON);
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)))
                .andDo(print());
    }

    @Test
    public void shouldUpdateEntityByGivenId() throws Exception {
        storeEntitiesInDb(1);
        T localEntity = getEntity();
        prepareNewEntity(getEntity());
        Field[] oldFields = getUtilService().collectEntityFields(localEntity);
        var request = put(testProperties.getResourceUrl() + Constants.RESOURCE_BY_ID, localEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(getEntity()));
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(localEntity.getId().toString()))
                .andExpect(jsonPath("$." + oldFields[0].getName()).value(not(getUtilService().getValueFromField(localEntity, oldFields[0].getName()))))
                .andExpect(jsonPath("$." + oldFields[1].getName()).value(not(getUtilService().getValueFromField(localEntity, oldFields[1].getName()))))
                .andDo(print());
    }

    @Test
    public void shouldDeleteEntityByGivenId() throws Exception {
        storeEntitiesInDb(1);
        var request = delete(testProperties.getResourceUrl() + Constants.RESOURCE_BY_ID, getEntity().getId())
                .contentType(MediaType.APPLICATION_JSON);
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void shouldReturnNotFoundEntityByGivenId() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        var request = get(testProperties.getResourceUrl() + Constants.RESOURCE_BY_ID, nonexistentId)
                .contentType(MediaType.APPLICATION_JSON);
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void shouldReturnNoContentWhenFilter() throws Exception {
        storeEntitiesInDb(2);
        Field[] fields = getUtilService().collectEntityFields(getEntity());
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add(Constants.PAGE, "0");
        paramsMap.add(Constants.SIZE, "10");
        paramsMap.add(Constants.SORT, "0");
        paramsMap.add(Constants.QUERY,  fields[0].getName() + "==" + RandomStringUtils.secure().nextAlphanumeric(10));
        var request = get(testProperties.getResourceUrl() + Constants.RESOURCE_SEARCH).params(paramsMap)
                .contentType(MediaType.APPLICATION_JSON);
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(true))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andDo(print());
    }

    @Test
    public void shouldReturnNotFoundUpdateEntity() throws Exception {
        storeEntitiesInDb(1);
        T localEntity = getEntity();
        Field field = utilService.getFieldFromEntity(localEntity, "id");
        if(field == null){
            return;
        }
        utilService.setValueByClassType(field, localEntity);
        prepareNewEntity(getEntity());
        var request = put(testProperties.getResourceUrl() + Constants.RESOURCE_BY_ID, localEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(getEntity()));
        if(testProperties.isAuth()){
            if (!setAuthentication(request)) return;
        }
        this.getMockMvc().perform(request)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
