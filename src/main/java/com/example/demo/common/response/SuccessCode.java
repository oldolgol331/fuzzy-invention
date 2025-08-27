package com.example.demo.common.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * PackageName : com.example.demo.common.response
 * FileName    : SuccessCode
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
public enum SuccessCode {

    // Common
    REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    CREATE_SUCCESS(HttpStatus.CREATED, "성공적으로 생성되었습니다."),
    UPDATE_SUCCESS(HttpStatus.OK, "성공적으로 업데이트되었습니다."),
    DELETE_SUCCESS(HttpStatus.OK, "성공적으로 삭제되었습니다."),

    // Member & Auth
    MEMBER_REGISTER_SUCCESS(HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다."),
    MEMBER_LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
    AUTHENTICATION_TOKEN_RENEW_SUCCESS(HttpStatus.OK, "인증 토큰이 성공적으로 갱신되었습니다."),
    MEMBER_INFO_FETCH_SUCCESS(HttpStatus.OK, "회원 정보를 성공적으로 조회했습니다."),
    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "이메일 인증이 성공적으로 완료되었습니다."),
    EMAIL_SENT(HttpStatus.OK, "이메일이 발송되었습니다."),
    UPDATE_MEMBER_INFO_SUCCESS(HttpStatus.OK, "회원 정보를 성공적으로 수정했습니다."),
    PASSWORD_CHANGED_SUCCESS(HttpStatus.OK, "비밀번호가 성공적으로 변경되었습니다."),
    MEMBER_WITHDRAWN_SUCCESS(HttpStatus.OK, "회원 탈퇴가 성공적으로 처리되었습니다."),

    // Post & PostLike
    POST_WRITE_SUCCESS(HttpStatus.CREATED, "성공적으로 게시글을 작성했습니다."),
    POST_READ_SUCCESS(HttpStatus.OK, "성공적으로 게시글을 조회했습니다."),
    POST_LIST_SEARCH_SUCCESS(HttpStatus.OK, "성공적으로 게시글 목록을 검색했습니다."),
    POST_UPDATE_SUCCESS(HttpStatus.OK, "성공적으로 게시글을 수정했습니다."),
    POST_DELETE_SUCCESS(HttpStatus.OK, "성공적으로 게시글을 삭제했습니다."),
    POST_LIKE_SUCCESS(HttpStatus.OK, "성공적으로 게시글에 좋아요를 추가/취소했습니다."),

    // Comment & CommentLike
    COMMENT_WRITE_SUCCESS(HttpStatus.CREATED, "성공적으로 댓글을 작성했습니다."),
    COMMENT_LIST_READ_SUCCESS(HttpStatus.OK, "성공적으로 댓글 목록을 조회했습니다."),
    COMMENT_UPDATE_SUCCESS(HttpStatus.OK, "성공적으로 댓글을 수정했습니다."),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "성공적으로 댓글을 삭제했습니다."),
    COMMENT_LIKE_SUCCESS(HttpStatus.OK, "성공적으로 댓글에 좋아요를 추가/취소했습니다.");

    private final HttpStatus status;
    private final String     message;

}
