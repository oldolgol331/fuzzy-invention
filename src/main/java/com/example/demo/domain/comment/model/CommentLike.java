package com.example.demo.domain.comment.model;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.demo.common.model.BaseAuditingEntity;
import com.example.demo.domain.member.model.Member;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.comment.model
 * FileName    : CommentLike
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Entity
@Table(name = "comment_likes")
@IdClass(CommentLikeId.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class CommentLike extends BaseAuditingEntity {

    @Id
    @Column(name = "member_id", nullable = false, insertable = false, updatable = false)
    private UUID memberId;

    @Id
    @Column(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Long commentId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "member_id", nullable = false, insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_comment_likes_members")
    )
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "comment_id", nullable = false, insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_comment_likes_comments")
    )
    private Comment comment;

    // ========================= Constructor Methods =========================

    /**
     * CommentLike 객체 생성
     *
     * @param member  - 회원
     * @param comment - 댓글
     * @return CommentLike 객체
     */
    public static CommentLike of(final Member member, final Comment comment) {
        CommentLike commentLike = CommentLike.builder().build();
        commentLike.setRelationshipWithMember(member);
        commentLike.setRelationshipWithComment(comment);
        return commentLike;
    }

    // ========================= Relationship Methods =========================

    /**
     * 회원과의 관계를 설정합니다.
     *
     * @param member - 회원
     */
    private void setRelationshipWithMember(final Member member) {
        this.member = member;
        member.getCommentLikes().add(this);
    }

    /**
     * 댓글과의 관계를 설정합니다.
     *
     * @param comment - 댓글
     */
    private void setRelationshipWithComment(final Comment comment) {
        this.comment = comment;
        comment.getCommentLikes().add(this);
    }

}
