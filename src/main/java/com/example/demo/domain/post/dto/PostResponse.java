package com.example.demo.domain.post.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.demo.domain.post.model.Post;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PackageName : com.example.demo.domain.post.dto
 * FileName    : PostResponse
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
@Schema(name = "Post 도메인 응답 DTO")
public class PostResponse {

    @Getter
    @Schema(name = "게시글 목록 정보 응답 DTO")
    public static class PostListResponse {

        @Schema(name = "게시글 ID")
        private final Long          id;
        @Schema(name = "작성자 ID")
        private final UUID          writerId;
        @Schema(name = "게시글 작성자 닉네임")
        private final String        writer;
        @Schema(name = "게시글 제목")
        private final String        title;
        @Schema(name = "삭제 여부")
        private final Boolean       isDeleted;
        @Schema(name = "생성 일시")
        private final LocalDateTime createdAt;
        @Schema(name = "최종 수정 일시")
        private final LocalDateTime updatedAt;
        @Setter
        @Schema(name = "조회수")
        private       Long          viewCount;
        @Setter
        @Schema(name = "좋아요 수")
        private       Integer       likeCount;
        @Setter
        @Schema(name = "댓글 수")
        private       Integer       commentCount;

        @Builder
        @QueryProjection
        @JsonCreator
        public PostListResponse(
                @JsonProperty("id") final Long id,
                @JsonProperty("writerId") final UUID writerId,
                @JsonProperty("writer") final String writer,
                @JsonProperty("title") final String title,
                @JsonProperty("viewCount") final Long viewCount,
                @JsonProperty("likeCount") final Integer likeCount,
                @JsonProperty("isDeleted") final Boolean isDeleted,
                @JsonProperty("createdAt") final LocalDateTime createdAt,
                @JsonProperty("updatedAt") final LocalDateTime updatedAt,
                @JsonProperty("commentCount") final Integer commentCount
        ) {
            this.id = id;
            this.writerId = writerId;
            this.writer = writer;
            this.title = title;
            this.viewCount = viewCount != null ? viewCount : 0L;
            this.likeCount = likeCount != null ? likeCount : 0;
            this.isDeleted = isDeleted;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.commentCount = commentCount != null ? commentCount : 0;
        }

        public static PostListResponse from(final Post post) {
            if (post == null) return null;
            return PostListResponse.builder()
                                   .id(post.getId())
                                   .writerId(post.getWriter().getId())
                                   .writer(post.getWriter().getNickname())
                                   .title(post.getTitle())
                                   .viewCount(post.getViewCount())
                                   .likeCount(post.getLikeCount())
                                   .isDeleted(post.getIsDeleted())
                                   .createdAt(post.getCreatedAt())
                                   .updatedAt(post.getUpdatedAt())
                                   .commentCount(post.getComments().size())
                                   .build();
        }

    }

    @Getter
    @Schema(name = "게시글 상세 정보 응답 DTO")
    public static class PostDetailResponse {

        @Schema(name = "게시글 ID")
        private final Long          id;
        @Schema(name = "작성자 ID")
        private final UUID          writerId;
        @Schema(name = "게시글 작성자 닉네임")
        private final String        writer;
        @Schema(name = "게시글 제목")
        private final String        title;
        @Schema(name = "게시글 내용")
        private final String        content;
        @Schema(name = "조회수")
        private final Long          viewCount;
        @Schema(name = "좋아요 수")
        private final Integer       likeCount;
        @Schema(name = "삭제 여부")
        private final Boolean       isDeleted;
        @Schema(name = "생성 일시")
        private final LocalDateTime createdAt;
        @Schema(name = "최종 수정 일시")
        private final LocalDateTime updatedAt;
        @Schema(name = "댓글 수")
        private final Integer       commentCount;
        @Schema(name = "작성자 여부")
        private final boolean       isWriter;

        @Builder
        @QueryProjection
        public PostDetailResponse(
                final Long id,
                final UUID writerId,
                final String writer,
                final String title,
                final String content,
                final Long viewCount,
                final Integer likeCount,
                final Boolean isDeleted,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt,
                final Integer commentCount,
                final boolean isWriter
        ) {
            this.id = id;
            this.writerId = writerId;
            this.writer = writer;
            this.title = title;
            this.content = content;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.isDeleted = isDeleted;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.commentCount = commentCount;
            this.isWriter = isWriter;
        }

    }

}
