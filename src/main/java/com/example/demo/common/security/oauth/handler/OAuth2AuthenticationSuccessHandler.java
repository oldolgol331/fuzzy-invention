package com.example.demo.common.security.oauth.handler;

import static com.example.demo.common.security.constant.SecurityConst.JWT_ACCESS_TOKEN_HEADER_NAME;
import static com.example.demo.common.security.constant.SecurityConst.JWT_REFRESH_TOKEN_COOKIE_NAME;

import com.example.demo.common.security.jwt.provider.JwtTokenProvider;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * PackageName : com.example.demo.common.security.oauth.handler
 * FileName    : OAuth2AuthenticationSuccessHandler
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${frontend.oauth2.authorized-redirect-uri}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        String accessToken  = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        response.setHeader(JWT_ACCESS_TOKEN_HEADER_NAME, accessToken);

        long refreshTokenExpirationSeconds = jwtTokenProvider.getRefreshTokenExpirationSeconds();

        ResponseCookie cookie = ResponseCookie.from(JWT_REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                                              .httpOnly(true)
                                              .secure(true)
                                              .path("/")
                                              .maxAge(refreshTokenExpirationSeconds)
                                              .sameSite("Lax")
                                              .build();
        response.addHeader("Set-Cookie", cookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                                               .queryParam("token", accessToken)
                                               .build()
                                               .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
