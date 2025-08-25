package com.example.demo.domain.member.dao;

import com.example.demo.domain.member.dto.MemberResponse.MemberInfoResponse;
import com.example.demo.domain.member.dto.QMemberResponse_MemberInfoResponse;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.MemberStatus;
import com.example.demo.domain.member.model.QMember;
import com.example.demo.domain.member.model.QOAuthConnection;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.example.demo.domain.member.dao
 * FileName    : MemberRepositoryImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private static final QMember          MEMBER           = QMember.member;
    private static final QOAuthConnection OAUTH_CONNECTION = QOAuthConnection.oAuthConnection;

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * OAuth 제공자와 고유 식별자로 회원을 찾습니다.
     *
     * @param provider   - OAuth 제공자
     * @param providerId - OAuth 고유 식별자
     * @return 회원 엔티티
     */
    @Override
    public Optional<Member> findByProviderAndProviderId(final String provider, final String providerId) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(MEMBER)
                               .join(OAUTH_CONNECTION.member, MEMBER)
                               .where(OAUTH_CONNECTION.provider.eq(provider), OAUTH_CONNECTION.providerId.eq(provider))
                               .fetchOne()
        );
    }

    /**
     * 회원 ID로 회원 정보를 반환합니다.
     *
     * @param id - 회원 ID
     * @return 회원 정보 응답 DTO
     */
    @Override
    public MemberInfoResponse getMemberInfoResponseById(final UUID id) {
        MemberInfoResponse content = jpaQueryFactory.select(
                                                            new QMemberResponse_MemberInfoResponse(
                                                                    MEMBER.id,
                                                                    MEMBER.email,
                                                                    MEMBER.nickname,
                                                                    MEMBER.memberRole,
                                                                    MEMBER.memberStatus,
                                                                    Expressions.constant(new ArrayList<>()),
                                                                    MEMBER.createdAt,
                                                                    MEMBER.updatedAt
                                                            )
                                                    )
                                                    .from(MEMBER)
                                                    .where(MEMBER.id.eq(id))
                                                    .fetchOne();
        if (content != null)
            content.addProviders(
                    jpaQueryFactory.select(OAUTH_CONNECTION.provider)
                                   .from(OAUTH_CONNECTION)
                                   .where(OAUTH_CONNECTION.member.id.eq(id))
                                   .fetch()
            );
        return content;
    }

    /**
     * 회원 ID, 회원 상태로 회원 정보를 반환합니다.
     *
     * @param id     - 회원 ID
     * @param status - 회원 상태
     * @return 회원 정보 응답 DTO
     */
    @Override
    public MemberInfoResponse getMemberInfoResponseByIdAndStatus(final UUID id, final MemberStatus status) {
        MemberInfoResponse content = jpaQueryFactory.select(
                                                            new QMemberResponse_MemberInfoResponse(
                                                                    MEMBER.id,
                                                                    MEMBER.email,
                                                                    MEMBER.nickname,
                                                                    MEMBER.memberRole,
                                                                    MEMBER.memberStatus,
                                                                    Expressions.constant(new ArrayList<>()),
                                                                    MEMBER.createdAt,
                                                                    MEMBER.updatedAt
                                                            )
                                                    )
                                                    .from(MEMBER)
                                                    .where(MEMBER.id.eq(id), MEMBER.memberStatus.eq(status))
                                                    .fetchOne();
        if (content != null)
            content.addProviders(
                    jpaQueryFactory.select(OAUTH_CONNECTION.provider)
                                   .from(OAUTH_CONNECTION)
                                   .where(OAUTH_CONNECTION.member.id.eq(id))
                                   .fetch()
            );
        return content;
    }

}
