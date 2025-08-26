package com.example.demo.common.security.service;

import static com.example.demo.common.response.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.example.demo.common.response.ErrorCode.TOKEN_MISMATCH;
import static com.example.demo.common.security.constant.SecurityConst.ACCESS_TOKEN_BLACKLIST_REDIS_PREFIX;
import static com.example.demo.common.security.constant.SecurityConst.REFRESH_TOKEN_REDIS_PREFIX;

import com.example.demo.common.error.CustomException;
import com.example.demo.common.security.jwt.provider.JwtTokenProvider;
import com.example.demo.common.security.model.CustomUserDetails;
import com.example.demo.infra.redis.dao.RedisRepository;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.common.security.service
 * FileName    : AuthService
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;
    private final RedisRepository       redisRepository;

    /**
     * 아이디와 비밀번호로 인증합니다.
     *
     * @param username - 아이디
     * @param password - 비밀번호
     * @return Authentication 객체
     */
    public Authentication signin(final String username, final String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    /**
     * 로그아웃을 처리합니다. 로그아웃된 사용자의 RefreshToken을 삭제하고, SecurityContextHolder를 비웁니다.
     *
     * @param userDetails - 사용자 정보
     * @param accessToken - AccessToken
     */
    public void signout(final CustomUserDetails userDetails, final String accessToken) {
        String redisKey = REFRESH_TOKEN_REDIS_PREFIX + userDetails.getId().toString();
        redisRepository.deleteData(redisKey);

        long remainingSeconds = jwtTokenProvider.getRemainingSeconds(accessToken);
        redisRepository.setValue(
                ACCESS_TOKEN_BLACKLIST_REDIS_PREFIX + accessToken,
                "logout",
                Duration.ofSeconds(remainingSeconds)
        );

        SecurityContextHolder.clearContext();
    }

    /**
     * RefreshToken으로 인증합니다. RefreshToken이 유효한지 검증하고, 유효하면 새로운 AccessToken을 생성합니다.
     *
     * @param refreshToken - RefreshToken
     * @return AccessToken
     */
    public String refresh(final String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) throw new CustomException(INVALID_REFRESH_TOKEN);

        UUID id = jwtTokenProvider.getMemberIdFromRefreshToken(refreshToken);

        String refreshTokenFromRedis = redisRepository.getValue(
                                                              REFRESH_TOKEN_REDIS_PREFIX + id.toString(), String.class
                                                      )
                                                      .orElseThrow(() -> new CustomException(INVALID_REFRESH_TOKEN));

        if (!refreshToken.equals(refreshTokenFromRedis)) throw new CustomException(TOKEN_MISMATCH);

        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(refreshToken);
        return jwtTokenProvider.generateAccessToken(authentication);
    }

}
