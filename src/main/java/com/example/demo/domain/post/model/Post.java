package com.example.demo.domain.post.model;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.demo.common.model.BaseAuditingEntity;
import com.example.demo.domain.member.model.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PackageName : com.example.demo.domain.post.model
 * FileName    : Post
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class Post extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "member_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_posts_members")
    )
    private Member writer;

    @Column(name = "title", nullable = false)
    @Setter
    @NotBlank
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    @Setter
    @NotBlank
    private String content;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    @Builder.Default
    private LocalDateTime deletedAt = null;

    @OneToMany(mappedBy = "post")
    @Builder.Default
    private List<PostLike> postLikes = new ArrayList<>();

    // ========================= Constructor Methods =========================

    public static Post of(final Member writer, final String title, final String content) {
        Post post = Post.builder()
                        .title(title)
                        .content(content)
                        .build();
        post.setRelationshipWithMember(writer);
        return post;
    }

    // ========================= JPA Callback Methods =========================

    /**
     * 좋아요 수를 업데이트합니다.
     */
    @PreUpdate
    private void updateLikeCount() {
        likeCount = postLikes.size();
    }

    // ========================= Relationship Methods =========================

    /**
     * 회원과의 관계를 설정합니다.
     *
     * @param member - 회원
     */
    private void setRelationshipWithMember(final Member member) {
        this.writer = member;
        member.getPosts().add(this);
    }

    // ========================= Business Methods =========================

    /**
     * 게시글을 삭제 처리합니다. 게시글의 삭제일을 현재 시간으로 설정합니다.
     */
    public void delete() {
        if (deletedAt != null || isDeleted) throw new IllegalStateException("이미 삭제된 게시글입니다.");
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }

}
