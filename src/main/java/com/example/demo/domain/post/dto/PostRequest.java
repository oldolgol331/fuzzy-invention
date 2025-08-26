package com.example.demo.domain.post.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.post.dto
 * FileName    : PostRequest
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
@Schema(name = "Post 도메인 요청 DTO")
public class PostRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "게시글 생성 요청 DTO")
    public static class PostCreateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        @Schema(name = "게시글 제목")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        @Schema(name = "게시글 내용")
        private String content;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "게시글 수정 요청 DTO")
    public static class PostUpdateRequest {

        @NotBlank(message = "새 제목은 필수입니다.")
        @Schema(name = "수정할 게시글 제목")
        private String newTitle;

        @NotBlank(message = "새 내용은 필수입니다.")
        @Schema(name = "수정할 게시글 내용")
        private String newContent;

    }

}
