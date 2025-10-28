/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.isysdcore.genericAutoCrud.utils;

/// Constants class that holds various constants used throughout the application.
/// This class is designed to provide a centralized location for commonly used
/// constants, such as API endpoint paths and default pagination parameters.
/// @author domingos.fernando
public class Constants {
    
    /**
     * Constants for the API endpoints
     * These constants define the base path for the API and the endpoints for
     * General search and list url
     * parameters
     * */
    public static final String RESOURCE_BY_ID = "/{id}";
    public static final String RESOURCE_SEARCH = "/search";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String SORT = "sort";
    public static final String QUERY = "query";

    public static final Integer PAGE_INDEX = 0;
    public static final Integer PAGE_SIZE = 10;
    public static final Integer PAGE_SORT = 1;
    public static final String AUTH_HEADER_NAME = "X-APP-SEC";
    public static final String TOKEN_TYPE = "Bearer";
}
