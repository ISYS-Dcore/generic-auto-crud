package io.github.isysdcore.genericAutoCrud.test.generics;

public interface RestIntegrationTests {
    void shouldCreateEntity() throws Exception; //: POST /api/entity
    void shouldReturnEntity() throws Exception;//: GET /api/entity/[:id]
    void shouldReturnListOfEntities() throws Exception;//: GET /api/entity
    void shouldReturnListOfEntitiesWithFilter() throws Exception;//: GET /api/entity?title=[:title]
    void shouldReturnNoContentWhenFilter() throws Exception;//: GET /api/entity?title=[:title] – 204
    void shouldUpdateEntity() throws Exception;//: PUT /api/entity/[:id]
    void shouldDeleteEntity() throws Exception;//: DELETE /api/entity/[:id]
    void shouldReturnNotFoundEntity() throws Exception;//: GET /api/entity/[:id] – 404
    void shouldReturnNotFoundUpdateEntity() throws Exception;//: PUT /api/entity/[:id] – 404
}
