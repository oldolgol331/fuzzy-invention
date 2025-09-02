package com.example.demo.infra.elasticsearch.post.dao;

import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.domain.post.service.PostCountServiceImpl.PostCountDto;
import com.example.demo.infra.elasticsearch.post.model.PostDocument;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
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

    /**
     * 검색된 게시글 목록을 반환합니다. 페이징 정보를 사용하여 결과를 페이지화하고, 검색어가 있을 경우 검색어를 포함한 게시글을 찾습니다.
     *
     * @param keyword  - 검색어
     * @param pageable - 페이징 정보
     * @return 게시글 페이징 목록 응답 DTO
     */
    @Override
    public Page<PostListResponse> getPosts(final String keyword, final Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.hasText(keyword))
            boolQueryBuilder.must(
                    QueryBuilders.multiMatchQuery(keyword, "title", "content")
                                 .fields(Collections.singletonMap("title", 3.0f))
            );

        Query nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
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
                                                                                           //.viewCount(
                                                                                           //        postDocument
                                                                                           //        .getViewCount()
                                                                                           //)
                                                                                           //.likeCount(
                                                                                           //        postDocument
                                                                                           //        .getLikeCount()
                                                                                           //)
                                                                                           .isDeleted(
                                                                                                   postDocument.getIsDeleted()
                                                                                           )
                                                                                           .createdAt(
                                                                                                   postDocument.getCreatedAt()
                                                                                           )
                                                                                           .updatedAt(
                                                                                                   postDocument.getUpdatedAt()
                                                                                           )
                                                                                           //.commentCount(
                                                                                           //        postDocument
                                                                                           //        .getCommentCount()
                                                                                           //)
                                                                                           .build()
                                                   )
                                                   .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

    /**
     * 게시글의 조회수를 조회합니다.
     *
     * @param postId - 게시글 ID
     * @return 조회수
     */
    @Override
    public Optional<Long> findViewCountById(final Long postId) {
        SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"viewCount"}, null);

        Query query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.idsQuery().addIds(String.valueOf(postId)))
                                                    .withSourceFilter(sourceFilter)
                                                    .build();

        SearchHit<PostDocument> searchHit = elasticsearchRestTemplate.searchOne(
                query, PostDocument.class, IndexCoordinates.of("posts")
        );

        if (searchHit == null) return Optional.empty();

        return Optional.of(searchHit.getContent().getViewCount());
    }

    /**
     * 게시글의 좋아요 수, 댓글 수를 조회합니다.
     *
     * @param postIds - 게시글 ID 목록
     * @return 게시글 ID와 좋아요 수, 댓글 수를 포함한 DTO
     */
    @Override
    public Map<Long, PostCountDto> findCountsById(final List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();

        SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"likeCount", "commentCount"}, null);

        Query query = new NativeSearchQueryBuilder().withQuery(
                                                            QueryBuilders.idsQuery()
                                                                         .addIds(
                                                                                 postIds.stream()
                                                                                        .map(String::valueOf)
                                                                                        .toArray(String[]::new)
                                                                         )
                                                    )
                                                    .withSourceFilter(sourceFilter)
                                                    .withPageable(PageRequest.of(0, postIds.size()))
                                                    .build();

        SearchHits<PostDocument> searchHits = elasticsearchRestTemplate.search(
                query, PostDocument.class, IndexCoordinates.of("posts")
        );

        return searchHits.getSearchHits()
                         .stream()
                         .map(SearchHit::getContent)
                         .collect(Collectors.toMap(
                                 doc -> Long.parseLong(doc.getId()),
                                 doc -> PostCountDto.of(0L, doc.getLikeCount(), doc.getCommentCount())
                         ));
    }

}
