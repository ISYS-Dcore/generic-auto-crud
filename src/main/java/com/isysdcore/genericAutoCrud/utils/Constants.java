/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isysdcore.genericAutoCrud.utils;

/**
 *
 * @author domingos.fernando
 */
public class Constants {
    /**
     * API informations details
     * */
    public static final String DEFAULT_URL_BASE = "/is";
    public static final String TOKEN_TYPE = "Bearer";
    public static final String DEFAULT_API_VERSION = "/v1";
    public static final String APP_API_AUTH = "/auth";
    public static final String APP_API_CORE = "/core";

    public static final String FULL_APP_API_CORE = DEFAULT_URL_BASE + DEFAULT_API_VERSION + APP_API_CORE;
    public static final String FULL_APP_API_AUTH = FULL_APP_API_CORE + APP_API_AUTH;
    public static final String API_VERSION = "1.2.0";
    public static final String API_VERSION_NAME = "Reconciliation";
    public static final String API_FULL_VERSION_NAME = API_VERSION + " - " + API_VERSION_NAME;

    /**
     * General search and list url
     * parameters
     * */
    public static final String RESOURCE_BY_ID = "/{id}";
    public static final String RESOURCE_SEARCH = "/search";
    public static final String SELL_PERIOD = "period";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String SORT = "sort";
    public static final String QUERY = "query";
    public static final String MONTH = "Month";
    public static final String WEEK = "Week";
    public static final String DAY = "Day";
    /**
     * SMS service definitions
     * parameters
     * */
    public static final String SMS_ACCESS_TOKEN = "Y5s66EFdgKfe25ceT6JHS25ds65";
    public static final String WAMY_REPLACEBLE_ARG = "@WRA";
    public static final String SMS_API_URL = "https://netsms.co.ao/app/appi/";
    /**
     * General configurations
     * parameters
     * */
    public static final String INVOICE_EMAIL_TEMPLATE = "IET";
    public static final String INVOICE_EMAIL_SUBJECT_TEMPLATE = "IEST";
    public static final String GENERAL_EMAIL_TEMPLATE = "GET";
    public static final String GENERAL_EMAIL_SUBJECT_TEMPLATE = "GEST";
    public static final String OTP_SMS_TEMPLATE = "OTPST";
    public static final String GENERAL_SMS_TEMPLATE = "GST";
    public static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
}
