package com.example.demo.domain.member.service;

import com.example.demo.domain.member.dto.MemberRequest.MemberPasswordUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberSignUpRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberWithdrawRequest;
import com.example.demo.domain.member.dto.MemberResponse.MemberInfoResponse;
import com.example.demo.domain.member.model.Member;
import java.util.UUID;

/**
 * PackageName : com.example.demo.domain.member.service
 * FileName    : MemberService
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
public interface MemberService {

    MemberInfoResponse signUpEmailUser(MemberSignUpRequest request);

    Member findOrCreateMemberForOAuth(
            String provider, String providerId, String emailFromOAuth, String nicknameFromOAuth
    );

    void resendVerificationEmail(String email);

    MemberInfoResponse verifyEmail(String token);

    MemberInfoResponse getMemberInfoById(UUID memberId);

    MemberInfoResponse updateMemberInfo(UUID memberId, MemberUpdateRequest request);

    void changePassword(UUID memberId, MemberPasswordUpdateRequest request);

    void withdrawMember(UUID memberId);

    void withdrawMember(UUID memberId, MemberWithdrawRequest request);

}
