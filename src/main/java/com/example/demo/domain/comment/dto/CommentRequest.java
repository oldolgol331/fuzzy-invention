package com.example.demo.domain.comment.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.comment.dto
 * FileName    : CommentRequest
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
@Schema(name = "Comment 도메인 요청 DTO")
public class CommentRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "댓글 생성 요청 DTO")
    public static class CommentCreateRequest {

        @NotBlank(message = "내용은 필수입니다.")
        @Schema(name = "댓글 내용")
        private String content;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "댓글 수정 요청 DTO")
    public static class CommentUpdateRequest {

        @NotBlank(message = "새 내용은 필수입니다.")
        @Schema(name = "수정할 댓글 내용")
        private String content;

    }

}
