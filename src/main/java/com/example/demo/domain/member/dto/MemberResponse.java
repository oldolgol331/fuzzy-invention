package com.example.demo.domain.member.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
    @RequiredArgsConstructor(access = PRIVATE)
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
        @Schema(name = "생성 일시")
        private final LocalDateTime createdAt;
        @Schema(name = "최종 수정 일시")
        private final LocalDateTime updatedAt;

        public static MemberInfoResponse of(
                final UUID id,
                final String email,
                final String nickname,
                final MemberRole role,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt
        ) {
            return MemberInfoResponse.builder()
                                     .id(id)
                                     .email(email)
                                     .nickname(nickname)
                                     .role(role)
                                     .createdAt(createdAt)
                                     .updatedAt(updatedAt)
                                     .build();
        }

        public static MemberInfoResponse from(final Member member) {
            return MemberInfoResponse.builder()
                                     .id(member.getId())
                                     .email(member.getEmail())
                                     .nickname(member.getNickname())
                                     .role(member.getMemberRole())
                                     .createdAt(member.getCreatedAt())
                                     .updatedAt(member.getUpdatedAt())
                                     .build();
        }

    }

}
