package com.example.demo.common.security.model;

import static com.example.demo.domain.member.model.MemberStatus.ACTIVE;
import static com.example.demo.domain.member.model.MemberStatus.BLOCKED;
import static lombok.AccessLevel.PRIVATE;

import com.example.demo.domain.member.model.MemberRole;
import com.example.demo.domain.member.model.MemberStatus;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * PackageName : com.example.demo.common.security.model
 * FileName    : CustomUserDetails
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@RequiredArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class CustomUserDetails implements UserDetails, OAuth2User {

    @Getter
    private final UUID                id;
    private final String              email;
    private final String              password;
    @Getter
    private final MemberRole          role;
    private final MemberStatus        status;
    private final Map<String, Object> attributes;

    public static CustomUserDetails of(
            final UUID id,
            final String email,
            final String password,
            final MemberRole role,
            final MemberStatus status
    ) {
        return CustomUserDetails.builder()
                                .id(id)
                                .email(email)
                                .password(password)
                                .role(role)
                                .status(status)
                                .build();
    }

    public static CustomUserDetails of(
            final UUID id,
            final String email,
            final MemberRole role,
            final MemberStatus status,
            final Map<String, Object> attributes
    ) {
        return CustomUserDetails.builder()
                                .id(id)
                                .email(email)
                                .role(role)
                                .status(status)
                                .attributes(attributes)
                                .build();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getRoleValue()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !(status != ACTIVE && status == BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == ACTIVE;
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("id"));
    }

}
