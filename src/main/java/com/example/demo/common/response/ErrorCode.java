package com.example.demo.common.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * PackageName : com.example.demo.common.response
 * FileName    : ErrorCode
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ErrorCode {

    //Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "CO001", "유효하지 않은 입력 값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "CO002", "유효하지 않은 타입 값입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "CO003", "해당 엔티티를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "CO004", "지원하지 않는 HTTP Method 입니다."),
    METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED, "CO005", "지원하지 않는 Content-Type 입니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "CO006", "요청 인자의 타입이 올바르지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "CO007", "접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "CO008", "인증 정보가 유효하지 않습니다."),
    CONSTRAINT_VIOLATION(HttpStatus.CONFLICT, "CO009", "데이터베이스 제약 조건 위반입니다."),
    BAD_SQL_GRAMMAR(HttpStatus.INTERNAL_SERVER_ERROR, "CO010", "잘못된 SQL 문법 오류가 발생했습니다."),
    REQUEST_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "CO011", "요청의 크기가 너무 큽니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "CO012", "너무 많은 요청을 보냈습니다. 잠시 후 다시 시도해주세요."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CO999", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."),

    //Member & Auth
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "ME001", "해당 회원을 찾을 수 없습니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "ME002", "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "ME003", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "ME004", "비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "ME004", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    MEMBER_INACTIVE(HttpStatus.FORBIDDEN, "ME006", "비활성화된 회원입니다. 관리자에게 문의하세요."),
    MEMBER_BLOCKED(HttpStatus.FORBIDDEN, "ME007", "차단된 회원입니다. 관리자에게 문의하세요."),
    MEMBER_ALREADY_WITHDRAWN(HttpStatus.FORBIDDEN, "ME008", "이미 탈퇴 처리된 회원입니다. 관리자에게 문의하세요."),
    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "ME009", "유효하지 않은 인증 토큰입니다."),
    ALREADY_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "ME010", "이미 인증된 이메일입니다."),
    PASSWORD_CHANGE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "ME011", "비밀번호 변경을 지원하지 않는 인증 타입입니다."),
    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "ME012", "지원하지 않는 OAuth 제공자입니다."),
    AUTH_TYPE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "ME013", "지원하지 않는 인증 타입입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ME013", "JWT RefreshToken이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ME014", "JWT RefreshToken이 만료되었습니다."),
    TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "ME015", "JWT 토큰이 일치하지 않습니다.");

    private final HttpStatus status;
    private final String     code;
    private final String     message;

}
