package com.example.demo.domain.member.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.member.dto
 * FileName    : MemberRequest
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
@Schema(name = "Member 도메인 요청 DTO")
public class MemberRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "로그인 요청 DTO")
    public static class MemberSignInRequest {

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효하지 않은 이메일 형식입니다.")
        @Schema(name = "이메일(아이디)")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(name = "비밀번호")
        private String password;

    }

}
