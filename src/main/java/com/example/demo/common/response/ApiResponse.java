package com.example.demo.common.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * PackageName : com.example.demo.common.response
 * FileName    : ApiResponse
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@Getter
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class ApiResponse<T> {

    @JsonIgnore
    private final HttpStatus status;
    @NotBlank
    private final String     message;
    private final T          data;

    public static <T> ApiResponse<T> of(final HttpStatus status, final String message) {
        return ApiResponse.<T>builder().status(status).message(message).build();
    }

    public static <T> ApiResponse<T> of(final HttpStatus status, final String message, final T data) {
        return ApiResponse.<T>builder().status(status).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> success(final SuccessCode successCode) {
        return ApiResponse.<T>builder().status(successCode.getStatus()).message(successCode.getMessage()).build();
    }

    public static <T> ApiResponse<T> success(final SuccessCode successCode, final T data) {
        return ApiResponse.<T>builder()
                          .status(successCode.getStatus())
                          .message(successCode.getMessage())
                          .data(data)
                          .build();
    }

    public static <T> ApiResponse<T> error(final ErrorCode errorCode) {
        return ApiResponse.<T>builder().status(errorCode.getStatus()).message(errorCode.getMessage()).build();
    }

    public static <T> ApiResponse<T> error(final ErrorCode errorCode, final String message) {
        return ApiResponse.<T>builder().status(errorCode.getStatus()).message(message).build();
    }

    public static <T> ApiResponse<T> error(final ErrorCode errorCode, final String message, final T data) {
        return ApiResponse.<T>builder().status(errorCode.getStatus()).message(message).data(data).build();
    }

}
