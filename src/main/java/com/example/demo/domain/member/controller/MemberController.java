package com.example.demo.domain.member.controller;

import static com.example.demo.common.response.SuccessCode.EMAIL_SENT;
import static com.example.demo.common.response.SuccessCode.EMAIL_VERIFICATION_SUCCESS;
import static com.example.demo.common.response.SuccessCode.MEMBER_INFO_FETCH_SUCCESS;
import static com.example.demo.common.response.SuccessCode.MEMBER_REGISTER_SUCCESS;
import static com.example.demo.common.response.SuccessCode.MEMBER_WITHDRAWN_SUCCESS;
import static com.example.demo.common.response.SuccessCode.PASSWORD_CHANGED_SUCCESS;
import static com.example.demo.common.response.SuccessCode.UPDATE_MEMBER_INFO_SUCCESS;
import static com.example.demo.common.security.constant.SecurityConst.JWT_ACCESS_TOKEN_HEADER_NAME;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.common.security.model.CustomUserDetails;
import com.example.demo.common.security.service.AuthService;
import com.example.demo.domain.member.dto.MemberRequest.MemberPasswordUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberSignUpRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberWithdrawRequest;
import com.example.demo.domain.member.dto.MemberRequest.ResendVerificationEmailRequest;
import com.example.demo.domain.member.dto.MemberResponse.MemberInfoResponse;
import com.example.demo.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackageName : com.example.demo.domain.member.controller
 * FileName    : MemberController
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService   authService;

    @PostMapping
    @Operation(summary = "회원 가입", description = "아이디(이메일)와 비밀번호로 회원 가입을 합니다.")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody final MemberSignUpRequest request) {
        memberService.signUpEmailUser(request);
        final SuccessCode successCode = MEMBER_REGISTER_SUCCESS;
        return ResponseEntity.status(successCode.getStatus())
                             .body(ApiResponse.success(successCode));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "이메일 인증", description = "발송된 메일에서 인증 토큰을 검증하고, 인증을 완료합니다.(유효 시간 내)")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam final String token) {
        memberService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(EMAIL_VERIFICATION_SUCCESS));
    }

    @PostMapping("/verify-email-resend")
    @Operation(summary = "인증 메일 재발송", description = "입력받은 이베일 주소로 인증 메일을 재발송합니다.(1분 내 재요청 금지)")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @Valid @RequestBody final ResendVerificationEmailRequest request
    ) {
        memberService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(EMAIL_SENT));
    }

    @GetMapping
    @Operation(summary = "회원 정보 조회", description = "로그인된 회원의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        MemberInfoResponse responseData = memberService.getMemberInfoById(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(MEMBER_INFO_FETCH_SUCCESS, responseData));
    }

    @PutMapping
    @Operation(summary = "회원 정보 수정", description = "로그인된 회원의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final MemberUpdateRequest request
    ) {
        MemberInfoResponse responseData = memberService.updateMemberInfo(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(UPDATE_MEMBER_INFO_SUCCESS, responseData));
    }

    @PatchMapping
    @Operation(summary = "비밀번호 변경", description = "로그인된 회원의 비밀번호를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @RequestHeader(JWT_ACCESS_TOKEN_HEADER_NAME) final String accessToken,
            @Valid @RequestBody final MemberPasswordUpdateRequest request
    ) {
        memberService.changePassword(userDetails.getId(), request);
        authService.signout(userDetails, accessToken.substring(7));
        return ResponseEntity.ok(ApiResponse.success(PASSWORD_CHANGED_SUCCESS));
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "로그인된 회원의 상태를 탈퇴로 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody(required = false) final MemberWithdrawRequest request,
            @RequestHeader(JWT_ACCESS_TOKEN_HEADER_NAME) final String accessToken
    ) {
        if (request != null && (request.getCurrentPassword() != null && !request.getCurrentPassword().trim().isEmpty()))
            memberService.withdrawMember(userDetails.getId(), request);
        else
            memberService.withdrawMember(userDetails.getId());
        authService.signout(userDetails, accessToken.substring(7));
        return ResponseEntity.ok(ApiResponse.success(MEMBER_WITHDRAWN_SUCCESS));
    }

}
