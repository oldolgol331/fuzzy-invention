package com.example.demo.domain.member.dao;

import com.example.demo.domain.member.dto.MemberResponse.MemberInfoResponse;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.MemberStatus;
import java.util.Optional;
import java.util.UUID;

/**
 * PackageName : com.example.demo.domain.member.dao
 * FileName    : MemberRepositoryCustom
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
public interface MemberRepositoryCustom {

    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    MemberInfoResponse getMemberInfoResponseById(UUID id);

    MemberInfoResponse getMemberInfoResponseByIdAndStatus(UUID id, MemberStatus status);

}
