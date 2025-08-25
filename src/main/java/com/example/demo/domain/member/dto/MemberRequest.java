package com.example.demo.domain.member.dto;

import static com.example.demo.domain.member.constant.MemberConst.EMAIL_REGEX;
import static com.example.demo.domain.member.constant.MemberConst.NICKNAME_REGEX;
import static com.example.demo.domain.member.constant.MemberConst.PASSWORD_REGEX;
import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "회원 가입 요청 DTO")
    public static class MemberSignUpRequest {

        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(
                regexp = EMAIL_REGEX,
                message = "유효하지 않은 이메일 형식입니다."
        )
        //@Email(message = "유효하지 않은 이메일 형식입니다.")
        @Schema(name = "이메일 주소(아이디)")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = PASSWORD_REGEX,
                message = "비밀번호는 8~20자 영문 대/소문자, 숫자, 특수문자를 반드시 1개씩 포함해서 사용하세요."
        )
        @Schema(name = "비밀번호")
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        @Schema(name = "비밀번호 확인")
        private String confirmPassword;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Pattern(
                regexp = NICKNAME_REGEX,
                message = "닉네임은 2~15자 영문, 한글, 숫자, '-', '_'만 사용 가능합니다."
        )
        @Schema(name = "닉네임")
        private String nickname;

        @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
        public boolean isPasswordConfirmed() {
            if (password == null || confirmPassword == null)
                return true;
            return password.equals(confirmPassword);
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "회원 정보 수정 요청 DTO")
    public static class MemberUpdateRequest {

        @NotBlank(message = "새 닉네임은 필수입니다.")
        @Pattern(
                regexp = NICKNAME_REGEX,
                message = "닉네임은 2~15자 영문, 한글, 숫자, '-', '_'만 사용 가능합니다."
        )
        @Schema(name = "수정할 닉네임")
        private String newNickname;

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        @Schema(name = "현재 비밀번호")
        private String currentPassword;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "회원 비밀번호 변경 요청 DTO")
    public static class MemberPasswordUpdateRequest {

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(
                regexp = PASSWORD_REGEX,
                message = "비밀번호는 8~20자 영문 대/소문자, 숫자, 특수문자를 반드시 1개씩 포함해서 사용하세요."
        )
        @Schema(name = "새 비밀번호")
        private String newPassword;

        @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
        @Schema(name = "새 비밀번호 확인")
        private String confirmNewPassword;

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        @Schema(name = "현재 비밀번호")
        private String currentPassword;

        @AssertTrue(message = "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.")
        public boolean isNewPasswordConfirmed() {
            if (newPassword == null || confirmNewPassword == null)
                return true;
            return newPassword.equals(confirmNewPassword);
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "인증 메일 재전송 요청 DTO")
    public static class ResendVerificationEmailRequest {

        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(
                regexp = EMAIL_REGEX,
                message = "유효하지 않은 이메일 형식입니다."
        )
        @Schema(name = "이메일 주소")
        private String email;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "회원 탈퇴 요청 DTO")
    public static class MemberWithdrawRequest {

        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        @Schema(name = "현재 비밀번호")
        private String currentPassword;

    }

}
