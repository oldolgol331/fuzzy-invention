package com.example.demo.common.security.oauth.service;

import com.example.demo.common.security.model.CustomUserDetails;
import com.example.demo.common.security.oauth.profile.OAuthProfile;
import com.example.demo.common.security.oauth.profile.OAuthProfileFactory;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.common.security.oauth.service
 * FileName    : CustomOAuth2UserService
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuthProfile oAuthProfile = OAuthProfileFactory.getOAuthProfile(registrationId, oAuth2User.getAttributes());

        Member member = memberService.findOrCreateMemberForOAuth(
                oAuthProfile.getProvider(),
                oAuthProfile.getProviderUserId(),
                oAuthProfile.getEmail(),
                oAuthProfile.getNickname()
        );

        return CustomUserDetails.of(
                member.getId(),
                member.getEmail(),
                member.getMemberRole(),
                member.getMemberStatus(),
                oAuth2User.getAttributes()
        );
    }

}
