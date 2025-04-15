package TiCatch.backend.global.constant;

public class UserConstants {
    public static final String ACTUAL_USERTYPE = "ACTUAL";
    public static final String VIRTUAL_USERTYPE = "VIRTUAL:";
    public static final Long VIRTUAL_USER_ID = 0L;
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ACCESS_TOKEN = "accessToken";
    public static final String HEADER_REFRESH_TOKEN = "refreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final int REFRESH_TOKEN_MAX_AGE = (7 * 24 * 60 * 60);
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60L * 40L;
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60L * 60L * 24L * 7L;
}
