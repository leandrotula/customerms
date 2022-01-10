package com.app.customer.security;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityConstants {

  public static final String LOGIN = "/v1/api/login";
  public static final String REFRESH_TOKEN_API = "/v1/api/token/refresh";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String ROLES = "roles";
  public static final String REFRESH_TOKEN = "refresh_token";
  public static final String ACCESS_TOKEN = "access_token";

}
