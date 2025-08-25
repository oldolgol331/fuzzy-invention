package com.example.demo.common.security.oauth.profile;

import static com.example.demo.common.response.ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED;
import static lombok.AccessLevel.PRIVATE;

import com.example.demo.common.error.CustomException;
import java.util.Map;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.common.security.oauth.profile
 * FileName    : OAuthProfileFactory
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public class OAuthProfileFactory {

    public static OAuthProfile getOAuthProfile(final String registrationId, final Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(registrationId)) return new GoogleProfile(attributes);
        if ("naver".equalsIgnoreCase(registrationId)) return new NaverProfile(attributes);
        if ("kakao".equalsIgnoreCase(registrationId)) return new KakaoProfile(attributes);
        throw new CustomException(OAUTH_PROVIDER_NOT_SUPPORTED);
    }

}
