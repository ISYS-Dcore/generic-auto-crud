package io.github.isysdcore.genericAutoCrud.generics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TestProperties {
    private String username;
    private String userPassword;
    private String authToken;
    private String authHeaderName;
    private String tokenType;
    private boolean auth;
    private String resourceUrl;
    private Object entityObj;
}
