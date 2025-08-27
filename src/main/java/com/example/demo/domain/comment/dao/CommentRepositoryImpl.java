package com.example.demo.domain.comment.dao;

import com.example.demo.domain.comment.dto.CommentResponse.CommentListResponse;
import com.example.demo.domain.comment.dto.QCommentResponse_CommentListResponse;
import com.example.demo.domain.comment.model.QComment;
import com.example.demo.domain.member.model.QMember;
import com.example.demo.domain.post.model.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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

/**
 * PackageName : com.example.demo.domain.comment.dao
 * FileName    : CommentRepositoryImpl
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
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private static final QComment COMMENT = QComment.comment;
    private static final QMember  MEMBER  = QMember.member;
    private static final QPost    POST    = QPost.post;

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 댓글 상세 정보를 조회합니다.
     *
     * @param postId   - 게시글 ID
     * @param writerId - 작성자 ID
     * @param pageable - 페이징 정보
     * @return 댓글 페이징 목록 응답 DTO
     */
    @Override
    public Page<CommentListResponse> getComments(final Long postId, final UUID writerId, final Pageable pageable) {
        List<CommentListResponse> content = jpaQueryFactory.select(
                                                                   new QCommentResponse_CommentListResponse(
                                                                           COMMENT.id,
                                                                           POST.id,
                                                                           MEMBER.id,
                                                                           MEMBER.nickname,
                                                                           COMMENT.content,
                                                                           COMMENT.likeCount,
                                                                           COMMENT.isDeleted,
                                                                           COMMENT.createdAt,
                                                                           COMMENT.updatedAt,
                                                                           COMMENT.writer.id.eq(writerId)
                                                                   )
                                                           )
                                                           .from(COMMENT)
                                                           .join(COMMENT.post, POST)
                                                           .join(COMMENT.writer, MEMBER)
                                                           .where(POST.id.eq(postId))
                                                           .orderBy(getSortCondition(pageable))
                                                           .offset(pageable.getOffset())
                                                           .limit(pageable.getPageSize())
                                                           .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(COMMENT.id.count())
                                                   .from(COMMENT)
                                                   .where(POST.id.eq(postId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // ========================= Private Methods =========================

    /**
     * 정렬 조건을 생성합니다. 기본적으로 최신 댓글부터 조회되며, 조회수, 좋아요 수 기준으로 정렬도 가능합니다.
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
                    case "likeCount":
                        orderSpecifiers.add(new OrderSpecifier(direction, COMMENT.likeCount));
                        break;
                    case "createdAt":
                        orderSpecifiers.add(new OrderSpecifier(direction, COMMENT.createdAt));
                        break;
                    default:
                        break;
                }
            });

        if (!hasCreatedAt) orderSpecifiers.add(new OrderSpecifier(Order.DESC, COMMENT.createdAt));

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}
