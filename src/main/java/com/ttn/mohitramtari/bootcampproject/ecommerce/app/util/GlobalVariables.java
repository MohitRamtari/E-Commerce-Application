package com.ttn.mohitramtari.bootcampproject.ecommerce.app.util;

public class GlobalVariables {

    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String MOBILE_NUMBER_REGEX = "^[6-9]\\d{9}$";
    public static final String EMAIL_REGEX = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
    public static final String POSTAL_CODE_REGEX = "^[1-9][0-9]{5}$";
    public static final String GST_REGEX = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";
    public static final String PAGE_SIZE_DEFAULT = "10";
    public static final String PAGE_OFFSET_DEFAULT = "0";
    public static final String SORT_PROPERTY_DEFAULT = "id";
    public static final String SORT_DIRECTION_DEFAULT = "asc";
    public static final long ACTIVATION_TOKEN_VALIDATION_TIME_IN_SECONDS = 10800L;
    public static final long RESET_PASSWORD_TOKEN_VALIDATION_TIME_IN_SECONDS = 900L;

    public static final String ADMIN_EMAIL = "mohit.ramtari.2001@gmail.com";
    public static final String SELLER_ACTIVATION_URL = "http://localhost:8080/admin/sellers/activate";
    public static final String PRODUCT_ACTIVATION_URL = "http://localhost:8080/admin/products/activate/";

    private GlobalVariables() {

    }
}