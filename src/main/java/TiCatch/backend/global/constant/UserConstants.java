package TiCatch.backend.global.constant;

public class UserConstants {
    public static final String ACTUAL_USERTYPE = "ACTUAL";
    public static final String VIRTUAL_USERTYPE = "VIRTUAL:";
    public static final Long VIRTUAL_USER_ID = 0L;
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ACCESS_TOKEN = "accessToken";
    public static final String HEADER_REFRESH_TOKEN = "refreshToken";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final int REFRESH_TOKEN_MAX_AGE = (7 * 24 * 60 * 60);
    public static final String REISSUE_REQUEST = "/api/auth/reissue";
    public static final String SWAGGER = "/swagger-ui";
    public static final String API_DOCS = "/v3/api-docs";
    public static final String WEBJARS = "/webjars";
    public static final String STATIC = "/static";
    public static final String FAVICON = "/favicon.ico";
    public static final String ERROR = "/error";
    public static final String LOGIN = "/api/auth/login/**";
    public static final String OAUTH2 = "/oauth2/**";
    public static final String LOGOUT = "/api/auth/logout";
    public static final String AUTH = "/auth";
    public static final String WAITING = "/api/ticket/waiting/**";
    public static final String ROOT = "/";
    public static final String OAUTH2_AUTHORIZATION = "/oauth2/authorization";
    public static final String OAUTH2_REDIRECTION = "/login/oauth2/code/*";
}
