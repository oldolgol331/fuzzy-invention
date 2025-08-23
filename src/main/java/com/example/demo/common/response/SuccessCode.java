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

    //Common
    REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    CREATE_SUCCESS(HttpStatus.CREATED, "성공적으로 생성되었습니다."),
    UPDATE_SUCCESS(HttpStatus.OK, "성공적으로 업데이트되었습니다."),
    DELETE_SUCCESS(HttpStatus.OK, "성공적으로 삭제되었습니다.");

    private final HttpStatus status;
    private final String     message;

}
