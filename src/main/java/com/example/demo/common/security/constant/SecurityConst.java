package com.example.demo.common.security.constant;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.common.security.constant
 * FileName    : SecurityConst
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public class SecurityConst {

    public static final String JWT_ACCESS_TOKEN_PREFIX       = "Bearer ";
    public static final String JWT_ACCESS_TOKEN_HEADER_NAME  = "Authorization";
    public static final String JWT_REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public static final String JWT_USERNAME_KEY    = "username";
    public static final String JWT_AUTHORITIES_KEY = "authorities";

    public static final String REFRESH_TOKEN_REDIS_PREFIX          = "rt:";
    public static final String ACCESS_TOKEN_BLACKLIST_REDIS_PREFIX = "bt:";

}
