package com.example.demo.domain.post.dao;

import com.example.demo.domain.member.model.QMember;
import com.example.demo.domain.post.dto.PostResponse.PostDetailResponse;
import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.domain.post.dto.QPostResponse_PostDetailResponse;
import com.example.demo.domain.post.dto.QPostResponse_PostListResponse;
import com.example.demo.domain.post.model.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * PackageName : com.example.demo.domain.post.dao
 * FileName    : PostRepositoryImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private static final QPost   POST   = QPost.post;
    private static final QMember MEMBER = QMember.member;

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시글 상세 정보를 조회합니다.
     *
     * @param postId - 게시글 ID
     * @return 게시글 상세 정보 응답 DTO
     */
    @Override
    public PostDetailResponse getPost(final Long postId, final UUID writerId) {
        return jpaQueryFactory.select(
                                      new QPostResponse_PostDetailResponse(
                                              POST.id,
                                              MEMBER.id,
                                              MEMBER.nickname,
                                              POST.title,
                                              POST.content,
                                              POST.viewCount,
                                              POST.likeCount,
                                              POST.isDeleted,
                                              POST.createdAt,
                                              POST.updatedAt,
                                              POST.writer.id.eq(writerId)
                                      )
                              )
                              .from(POST)
                              .join(POST.writer, MEMBER)
                              .where(POST.id.eq(postId))
                              .fetchOne();
    }

    /**
     * 게시글 검색 결과를 조회합니다.
     *
     * @param keyword  - 검색어
     * @param pageable - 페이징 정보
     * @return 게시글 페이징 목록 응답 DTO
     */
    @Override
    public Page<PostListResponse> getPosts(final String keyword, final Pageable pageable) {
        List<Long> postIds = jpaQueryFactory.select(POST.id)
                                            .from(POST)
                                            .where(matchAgainstKeyword(keyword))
                                            .orderBy(getSortCondition(pageable))
                                            .offset(pageable.getOffset())
                                            .limit(pageable.getPageSize())
                                            .fetch();
        if (postIds.isEmpty()) return Page.empty();

        List<PostListResponse> content = jpaQueryFactory.select(
                                                                new QPostResponse_PostListResponse(
                                                                        POST.id,
                                                                        MEMBER.id,
                                                                        MEMBER.nickname,
                                                                        POST.title,
                                                                        POST.viewCount,
                                                                        POST.likeCount,
                                                                        POST.isDeleted,
                                                                        POST.createdAt,
                                                                        POST.updatedAt
                                                                )
                                                        )
                                                        .from(POST)
                                                        .join(POST.writer, MEMBER)
                                                        .where(POST.id.in(postIds))
                                                        .orderBy(getSortCondition(pageable))
                                                        .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(POST.id.count())
                                                   .from(POST)
                                                   .where(matchAgainstKeyword(keyword));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // ========================= Private Methods =========================

    /**
     * 검색어와 일치하는 게시글을 찾는데 사용되는 조건식(FULLTEXT INDEX MATCH() AGAINST() BOOLEAN MODE)입니다.
     *
     * @param keyword - 검색어
     * @return 조건식
     */
    private BooleanExpression matchAgainstKeyword(final String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return Expressions.numberTemplate(
                Double.class,
                "function('fulltext_boolean_search_param_2', {0}, {1}, {2})",
                POST.title,
                POST.content,
                "+" + keyword.replaceAll("\\s+", " +")
        ).gt(0);
    }

    /**
     * 정렬 조건을 생성합니다. 기본적으로 최신 게시글부터 조회되며, 조회수, 좋아요 수 기준으로 정렬도 가능합니다.
     *
     * @param pageable - 페이징 정보
     * @return 정렬 조건 배열
     */
    private OrderSpecifier<?>[] getSortCondition(final Pageable pageable) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        boolean hasCreatedAt = pageable.getSort().stream().anyMatch(order -> "createdAt".equals(order.getProperty()));

        if (!pageable.getSort().isEmpty())
            pageable.getSort().forEach(order -> {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "viewCount":
                        orderSpecifiers.add(new OrderSpecifier(direction, POST.viewCount));
                        break;
                    case "likeCount":
                        orderSpecifiers.add(new OrderSpecifier(direction, POST.likeCount));
                        break;
                    case "createdAt":
                        orderSpecifiers.add(new OrderSpecifier(direction, POST.createdAt));
                        break;
                    default:
                        break;
                }
            });

        if (!hasCreatedAt) orderSpecifiers.add(new OrderSpecifier(Order.DESC, POST.createdAt));

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}
