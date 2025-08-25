package com.example.demo.domain.member.model;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.demo.common.model.BaseAuditingEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.member.model
 * FileName    : OAuthConnection
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@Entity
@Table(
        name = "oauth_connections",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_oauth_connections_provider_provider_id", columnNames = {"provider", "provider_id"}
                ),
                @UniqueConstraint(
                        name = "UK_oauth_connections_member_id_provider", columnNames = {"member_id", "provider"}
                )
        }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class OAuthConnection extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "oauth_connection_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "member_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_oauth_connections_members")
    )
    private Member member;

    @Column(name = "provider", nullable = false, updatable = false)
    @NotBlank
    private String provider;

    @Column(name = "provider_id", nullable = false, updatable = false)
    @NotBlank
    private String providerId;

    @Column(name = "deleted_at")
    @Builder.Default
    private LocalDateTime deletedAt = null;

    // ========================= Constructor Methods =========================

    /**
     * OAuthConnection 객체 생성
     *
     * @param member     - 회원
     * @param provider   - OAuth2 제공자
     * @param providerId - OAuth2 고유 식별자
     * @return OAuthConnection 객체
     */
    public static OAuthConnection of(final Member member, final String provider, final String providerId) {
        OAuthConnection oAuthConnection = OAuthConnection.builder()
                                                         .provider(provider)
                                                         .providerId(providerId)
                                                         .build();
        oAuthConnection.setRelationshipWithMember(member);
        return oAuthConnection;
    }

    // ========================= Relationship Methods =========================

    /**
     * 회원과의 관계를 설정합니다.
     *
     * @param member - 회원
     */
    private void setRelationshipWithMember(final Member member) {
        this.member = member;
        member.getOAuthConnections().add(this);
    }

}
