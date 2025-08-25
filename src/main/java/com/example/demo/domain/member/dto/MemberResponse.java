package com.example.demo.domain.member.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.MemberRole;
import com.example.demo.domain.member.model.MemberStatus;
import com.example.demo.domain.member.model.OAuthConnection;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.member.dto
 * FileName    : MemberResponse
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
@Schema(name = "Member 도메인 응답 DTO")
public class MemberResponse {

    @Getter
    @Builder(access = PRIVATE)
    @Schema(name = "회원 정보 응답 DTO")
    public static class MemberInfoResponse {

        @Schema(name = "회원 ID")
        private final UUID          id;
        @Schema(name = "이메일 주소(아이디)")
        private final String        email;
        @Schema(name = "닉네임")
        private final String        nickname;
        @Schema(name = "회원 역할: [USER, ADMIN]")
        private final MemberRole    role;
        @Schema(name = "회원 상태: [ACTIVE, INACTIVE, DELETED, BLOCKED]")
        private final MemberStatus  status;
        @Schema(name = "OAuth 인증 제공자 목록")
        private final List<String>  providers;
        @Schema(name = "생성 일시")
        private final LocalDateTime createdAt;
        @Schema(name = "최종 수정 일시")
        private final LocalDateTime updatedAt;

        @Builder
        @QueryProjection
        public MemberInfoResponse(
                final UUID id,
                final String email,
                final String nickname,
                final MemberRole role,
                final MemberStatus status,
                final List<String> providers,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt
        ) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.role = role;
            this.status = status;
            this.providers = providers;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static MemberInfoResponse of(
                final UUID id,
                final String email,
                final String nickname,
                final MemberRole role,
                final MemberStatus status,
                final List<String> providers,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt
        ) {
            return MemberInfoResponse.builder()
                                     .id(id)
                                     .email(email)
                                     .nickname(nickname)
                                     .role(role)
                                     .status(status)
                                     .providers(providers)
                                     .createdAt(createdAt)
                                     .updatedAt(updatedAt)
                                     .build();
        }

        public static MemberInfoResponse from(final Member member) {
            if (member == null) return null;
            return MemberInfoResponse.builder()
                                     .id(member.getId())
                                     .email(member.getEmail())
                                     .nickname(member.getNickname())
                                     .role(member.getMemberRole())
                                     .status(member.getMemberStatus())
                                     .providers(member.getOAuthConnections()
                                                      .stream()
                                                      .map(OAuthConnection::getProvider)
                                                      .collect(Collectors.toList()))
                                     .createdAt(member.getCreatedAt())
                                     .updatedAt(member.getUpdatedAt())
                                     .build();
        }

        public void addProviders(final List<String> providers) {
            this.providers.addAll(providers);
        }

    }

}
