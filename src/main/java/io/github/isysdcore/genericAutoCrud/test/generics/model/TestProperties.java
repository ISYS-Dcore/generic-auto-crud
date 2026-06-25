package io.github.isysdcore.genericAutoCrud.test.generics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class TestProperties {
    private String username;
    private String userPassword;
    private String authToken;
    private String authHeaderName;
    private String tokenType;
    @Setter
    private boolean auth;
    private String resourceUrl;
    private Object entityObj;
}
