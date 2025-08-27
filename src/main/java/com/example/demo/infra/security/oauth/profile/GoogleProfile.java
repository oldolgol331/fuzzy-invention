package com.example.demo.infra.security.oauth.profile;

import java.util.Map;

/**
 * PackageName : com.example.demo.infra.security.oauth.profile
 * FileName    : GoogleProfile
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
public class GoogleProfile extends OAuthProfile {

    protected GoogleProfile(final Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderUserId() {
        return (String) getAttributes().get("sub");
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("name");
    }

    @Override
    public String getProvider() {
        return "GOOGLE";
    }

}
