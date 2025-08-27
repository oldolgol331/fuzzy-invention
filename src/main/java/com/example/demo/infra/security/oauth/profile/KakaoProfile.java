package com.example.demo.infra.security.oauth.profile;

import java.util.Map;

/**
 * PackageName : com.example.demo.infra.security.oauth.profile
 * FileName    : KakaoProfile
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
public class KakaoProfile extends OAuthProfile {

    private final Map<String, Object> kakaoAccountAttributes;
    private final Map<String, Object> profileAttributes;

    @SuppressWarnings("unchecked")
    protected KakaoProfile(final Map<String, Object> attributes) {
        super(attributes);
        kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        profileAttributes = (Map<String, Object>) kakaoAccountAttributes.get("profile");
    }

    @Override
    public String getProviderUserId() {
        return String.valueOf(getAttributes().get("id"));
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccountAttributes.get("email");
    }

    @Override
    public String getNickname() {
        return (String) profileAttributes.get("nickname");
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

}
