package com.example.demo.infra.security.oauth.profile;

import java.util.Map;

/**
 * PackageName : com.example.demo.infra.security.oauth.profile
 * FileName    : NaverProfile
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
public class NaverProfile extends OAuthProfile {

    private final Map<String, Object> responseAttributes;

    @SuppressWarnings("unchecked")
    protected NaverProfile(final Map<String, Object> attributes) {
        super(attributes);
        responseAttributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderUserId() {
        return (String) responseAttributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) responseAttributes.get("email");
    }

    @Override
    public String getNickname() {
        return (String) responseAttributes.get("nickname");
    }

    @Override
    public String getProvider() {
        return "NAVER";
    }

}
