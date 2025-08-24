package com.example.demo.common.security.service;

import com.example.demo.common.security.model.CustomUserDetails;
import com.example.demo.domain.member.dao.MemberRepository;
import com.example.demo.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.common.security.service
 * FileName    : CustomUserDetailsService
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
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmailAndDeletedAtNotNull(username.toLowerCase())
                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                "회원이 존재하지 않습니다: " + username.toLowerCase()
                                        ));
        return CustomUserDetails.of(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getMemberRole(),
                member.getDeletedAt()
        );
    }

}
