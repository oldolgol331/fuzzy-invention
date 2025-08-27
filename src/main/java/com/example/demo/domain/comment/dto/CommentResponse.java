package com.example.demo.domain.comment.dto;

import static lombok.AccessLevel.PRIVATE;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.comment.dto
 * FileName    : CommentResponse
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
@Schema(name = "Comment 도메인 응답 DTO")
public class CommentResponse {

    @Getter
    @Schema(name = "댓글 목록 정보 응답 DTO")
    public static class CommentListResponse {

        @Schema(name = "댓글 ID")
        private final Long          id;
        @Schema(name = "게시글 ID")
        private final Long          postId;
        @Schema(name = "작성자 ID")
        private final UUID          writerId;
        @Schema(name = "댓글 작성자 닉네임")
        private final String        writer;
        @Schema(name = "댓글 내용")
        private final String        content;
        @Schema(name = "좋아요 수")
        private final Integer       likeCount;
        @Schema(name = "삭제 여부")
        private final Boolean       isDeleted;
        @Schema(name = "생성 일시")
        private final LocalDateTime createdAt;
        @Schema(name = "최종 수정 일시")
        private final LocalDateTime updatedAt;
        @Schema(name = "작성자 여부")
        private final boolean       isWriter;

        @Builder
        @QueryProjection
        public CommentListResponse(
                final Long id,
                final Long postId,
                final UUID writerId,
                final String writer,
                final String content,
                final Integer likeCount,
                final Boolean isDeleted,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt,
                final boolean isWriter
        ) {
            this.id = id;
            this.postId = postId;
            this.writerId = writerId;
            this.writer = writer;
            this.content = content;
            this.likeCount = likeCount;
            this.isDeleted = isDeleted;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isWriter = isWriter;
        }

    }

}
