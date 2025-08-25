package com.example.demo.domain.post.model;

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
 * PackageName : com.example.demo.domain.post.model
 * FileName    : PostLike
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Entity
@Table(name = "post_likes")
@IdClass(PostLikeId.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class PostLike extends BaseAuditingEntity {

    @Id
    @Column(name = "member_id", nullable = false, insertable = false, updatable = false)
    private UUID memberId;

    @Id
    @Column(name = "post_id", nullable = false, insertable = false, updatable = false)
    private Long postId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "member_id", nullable = false, insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_post_likes_members")
    )
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "post_id", nullable = false, insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_post_likes_posts")
    )
    private Post post;

    // ========================= Constructor Methods =========================

    /**
     * PostLike 객체 생성
     *
     * @param member - 회원
     * @param post   - 게시글
     * @return PostLike 객체
     */
    public static PostLike of(final Member member, final Post post) {
        PostLike postLike = PostLike.builder().build();
        postLike.setRelationshipWithMember(member);
        postLike.setRelationshipWithPost(post);
        return postLike;
    }

    // ========================= Relationship Methods =========================

    /**
     * 회원과의 관계를 설정합니다.
     *
     * @param member - 회원
     */
    private void setRelationshipWithMember(final Member member) {
        this.member = member;
        member.getPostLikes().add(this);
    }

    /**
     * 게시글과의 관계를 설정합니다.
     *
     * @param post - 게시글
     */
    private void setRelationshipWithPost(final Post post) {
        this.post = post;
        post.getPostLikes().add(this);
    }

}
