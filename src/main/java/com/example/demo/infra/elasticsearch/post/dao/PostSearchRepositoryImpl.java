package com.example.demo.infra.elasticsearch.post.dao;

import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.infra.elasticsearch.post.model.PostDocument;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * PackageName : com.example.demo.infra.elasticsearch.post.dao
 * FileName    : PostSearchRepositoryImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Repository
@RequiredArgsConstructor
public class PostSearchRepositoryImpl implements PostSearchRepositoryCustom {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<PostListResponse> getPosts(final String keyword, final Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.hasText(keyword))
            boolQueryBuilder.must(
                    QueryBuilders.multiMatchQuery(keyword, "title", "content")
                                 .fields(Collections.singletonMap("title", 3.0f))
            );

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                                                                            .withPageable(pageable)
                                                                            .build();

        SearchHits<PostDocument> searchHits = elasticsearchRestTemplate.search(
                nativeSearchQuery, PostDocument.class, IndexCoordinates.of("posts")
        );
        List<PostListResponse> content = searchHits.getSearchHits()
                                                   .stream()
                                                   .map(SearchHit::getContent)
                                                   .map(
                                                           postDocument -> PostListResponse.builder()
                                                                                           .id(Long.parseLong(
                                                                                                   postDocument.getId()
                                                                                           ))
                                                                                           .writerId(
                                                                                                   postDocument.getWriterId()
                                                                                           )
                                                                                           .writer(postDocument.getWriter())
                                                                                           .title(postDocument.getTitle())
                                                                                           .viewCount(
                                                                                                   postDocument.getViewCount()
                                                                                           )
                                                                                           .likeCount(
                                                                                                   postDocument.getLikeCount()
                                                                                           )
                                                                                           .isDeleted(
                                                                                                   postDocument.getIsDeleted()
                                                                                           )
                                                                                           .createdAt(
                                                                                                   postDocument.getCreatedAt()
                                                                                           )
                                                                                           .updatedAt(
                                                                                                   postDocument.getUpdatedAt()
                                                                                           )
                                                                                           .commentCount(
                                                                                                   postDocument.getCommentCount()
                                                                                           )
                                                                                           .build()
                                                   )
                                                   .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

}
