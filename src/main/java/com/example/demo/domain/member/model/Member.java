package com.example.demo.domain.member.model;

import static com.example.demo.domain.member.constant.MemberConst.EMAIL_PATTERN;
import static com.example.demo.domain.member.constant.MemberConst.NICKNAME_PATTERN;
import static com.example.demo.domain.member.model.MemberRole.USER;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.demo.common.model.BaseAuditingEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * PackageName : com.example.demo.domain.member.model
 * FileName    : Member
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
        name = "members",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_members_email", columnNames = "email"),
                @UniqueConstraint(name = "UK_members_nickname", columnNames = "nickname")
        }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class Member extends BaseAuditingEntity {

    @Id
    @GeneratedValue(generator = "ulid_generator")
    @GenericGenerator(
            name = "ulid_generator",
            strategy = "com.example.demo.common.model.generator.UlidGenerator"
    )
    @Type(type = "uuid-binary")
    @Column(name = "member_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false)
    private MemberRole memberRole;

    @Column(name = "deleted_at")
    @Builder.Default
    private LocalDateTime deletedAt = null;

    // ========================= Constructor Methods =========================

    /**
     * Member 객체 생성
     *
     * @param email    - 이메일
     * @param password - 비밀번호
     * @param nickname - 닉네임
     * @return Member 객체
     */
    public static Member of(final String email, final String password, final String nickname) {
        return of(email, password, nickname, USER);
    }

    /**
     * Member 객체 생성
     *
     * @param email      - 이메일
     * @param password   - 비밀번호
     * @param nickname   - 닉네임
     * @param memberRole - 회원 권한
     * @return Member 객체
     */
    public static Member of(
            final String email, final String password, final String nickname, final MemberRole memberRole
    ) {
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
        return Member.builder()
                     .email(email.toLowerCase())
                     .password(password)
                     .nickname(nickname)
                     .memberRole(memberRole)
                     .build();
    }

    // ========================= JPA Callback Methods =========================

    /**
     * 이메일 유효성을 검사합니다.
     *
     * @param email - 이메일
     */
    private static void validateEmail(final String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches())
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }

    // ========================= Validation Methods =========================

    /**
     * 비밀번호 유효성을 검사합니다.
     *
     * @param password - 비밀번호
     */
    private static void validatePassword(final String password) {
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }

    /**
     * 닉네임 유효성을 검사합니다.
     *
     * @param nickname - 닉네임
     */
    private static void validateNickname(final String nickname) {
        if (nickname == null || nickname.trim().isEmpty())
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        if (!NICKNAME_PATTERN.matcher(nickname).matches())
            throw new IllegalArgumentException("닉네임 형식이 올바르지 않습니다. 2~15자 영문, 한글, 숫자, '-', '_'만 가능합니다.");
    }

    /**
     * 이메일을 소문자로 변환하여 저장합니다.
     */
    @PrePersist
    @PreUpdate
    private void convertEmailToLowerCase() {
        if (email != null) email = email.toLowerCase();
    }

    // ========================= Business Methods =========================

    /**
     * 비밀번호를 변경합니다.
     *
     * @param password - 비밀번호
     */
    public void setPassword(final String password) {
        validatePassword(password);
        this.password = password;
    }

    /**
     * 닉네임을 변경합니다.
     *
     * @param nickname - 닉네임
     */
    public void setNickname(final String nickname) {
        validatePassword(nickname);
        this.nickname = nickname;
    }

    /**
     * 회원 권한을 변경합니다.
     *
     * @param memberRole - 회원 권한
     */
    public void setMemberRole(final MemberRole memberRole) {
        if (memberRole == null) throw new IllegalArgumentException("회원 권한은 필수입니다.");
        this.memberRole = memberRole;
    }

    /**
     * 회원을 탈퇴 처리합니다. 회원의 탈퇴일을 현재 시간으로 설정합니다.
     */
    public void withdraw() {
        if (deletedAt != null) throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        deletedAt = LocalDateTime.now();
    }

}
