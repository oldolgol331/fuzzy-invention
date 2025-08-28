package com.example.demo.infra.elasticsearch.post.model;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.elasticsearch.annotations.FieldType.Binary;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import com.example.demo.domain.post.model.Post;
import com.example.demo.domain.post.model.PostDto;
import com.example.demo.infra.elasticsearch.common.document.ElasticsearchDocument;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * PackageName : com.example.demo.infra.elasticsearch.post.model
 * FileName    : PostDocument
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Document(indexName = "posts", createIndex = true)
@Getter
@RequiredArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class PostDocument extends ElasticsearchDocument {

    @Id
    private final Long id;

    @Field(type = Binary)
    private final UUID writerId;

    @Field(type = Keyword)
    private final String writer;

    @Field(type = Text, analyzer = "nori")
    private final String title;

    @Field(type = Text, analyzer = "nori")
    private final String content;

    @Field(type = FieldType.Long)
    private final Long viewCount;

    @Field(type = FieldType.Integer)
    private final Integer likeCount;

    @Field(type = FieldType.Boolean)
    private final Boolean isDeleted;

    @Field(type = Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]||epoch_millis")
    private final LocalDateTime createdAt;

    @Field(type = Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]||epoch_millis")
    private final LocalDateTime updatedAt;

    @Field(type = FieldType.Integer)
    private final Integer commentCount;

    public static PostDocument from(final Post post) {
        return PostDocument.builder()
                           .id(post.getId())
                           .writerId(post.getWriter().getId())
                           .writer(post.getWriter().getNickname())
                           .title(post.getTitle())
                           .content(post.getContent())
                           .viewCount(post.getViewCount())
                           .likeCount(post.getLikeCount())
                           .isDeleted(post.getIsDeleted())
                           .createdAt(post.getCreatedAt())
                           .updatedAt(post.getUpdatedAt())
                           .commentCount(post.getComments().size())
                           .build();
    }

    public static PostDocument from(final PostDto postDto) {
        return PostDocument.builder()
                           .id(postDto.getId())
                           .writerId(postDto.getWriterId())
                           .writer(postDto.getWriter())
                           .title(postDto.getTitle())
                           .content(postDto.getContent())
                           .viewCount(postDto.getViewCount())
                           .likeCount(postDto.getLikeCount())
                           .isDeleted(postDto.getIsDeleted())
                           .createdAt(postDto.getCreatedAt())
                           .updatedAt(postDto.getUpdatedAt())
                           .commentCount(postDto.getCommentCount())
                           .build();
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

}
