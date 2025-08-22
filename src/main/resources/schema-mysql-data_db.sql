SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS oauth_connections CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS post_likes CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS comment_likes CASCADE;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE members
(
    member_id  BINARY(16)   NOT NULL COMMENT '회원 고유 식별자',
    email      VARCHAR(255) NOT NULL COMMENT '회원 이메일',
    password   VARCHAR(255) NULL COMMENT '암호화된 비밀번호 (OAuth2 회원은 NULL)',
    nickname   VARCHAR(255) NOT NULL COMMENT '회원 닉네임',
    role       VARCHAR(255) NOT NULL DEFAULT 'USER' COMMENT '회원 권한 (USER, ADMIN)',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    deleted_at DATETIME     NULL COMMENT '삭제 일시',
    CONSTRAINT PK_members PRIMARY KEY (member_id),
    CONSTRAINT UK_members_email UNIQUE (email),
    CONSTRAINT UK_members_nickname UNIQUE (nickname)
) COMMENT '회원 테이블';

CREATE TABLE oauth_connections
(
    oauth_connection_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'OAuth 연결 고유 식별자',
    member_id           BINARY(16)      NOT NULL COMMENT '연결된 회원 ID',
    provider            VARCHAR(255)    NOT NULL COMMENT 'OAuth2 제공자 (GOOGLE, NAVER, KAKAO)',
    provider_id         VARCHAR(255)    NOT NULL COMMENT 'OAuth2 제공자가 발급한 고유 식별자',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    deleted_at          DATETIME        NULL COMMENT '삭제 일시',
    CONSTRAINT PK_oauth_connections PRIMARY KEY (oauth_connection_id),
    CONSTRAINT FK_oauth_connections_members FOREIGN KEY (member_id) REFERENCES members (member_id),
    CONSTRAINT UK_oauth_connections_provider_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT UK_oauth_connections_member_id_provider UNIQUE (member_id, provider)
) COMMENT 'OAuth 연결 테이블';

CREATE TABLE posts
(
    post_id    BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '게시글 고유 식별자',
    member_id  BINARY(16)       NOT NULL COMMENT '작성한 회원 ID',
    title      VARCHAR(255)     NOT NULL COMMENT '게시글 제목',
    content    TEXT             NOT NULL COMMENT '게시글 내용',
    view_count BIGINT UNSIGNED  NOT NULL DEFAULT 0 COMMENT '조회수',
    like_count INTEGER UNSIGNED NOT NULL DEFAULT 0 COMMENT '추천수',
    created_at DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    deleted_at DATETIME         NULL COMMENT '삭제 일시',
    CONSTRAINT PK_posts PRIMARY KEY (post_id),
    CONSTRAINT FK_posts_members FOREIGN KEY (member_id) REFERENCES members (member_id)
) COMMENT '게시글 테이블';

CREATE TABLE post_likes
(
    member_id  BINARY(16)      NOT NULL COMMENT '좋아요를 누른 회원 ID',
    post_id    BIGINT UNSIGNED NOT NULL COMMENT '좋아요를 받은 게시글 ID',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    CONSTRAINT PK_post_likes PRIMARY KEY (member_id, post_id),
    CONSTRAINT FK_post_likes_members FOREIGN KEY (member_id) REFERENCES members (member_id),
    CONSTRAINT FK_post_likes_posts FOREIGN KEY (post_id) REFERENCES posts (post_id)
) COMMENT '게시글 좋아요 테이블';

CREATE TABLE comments
(
    comment_id BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '댓글 고유 식별자',
    post_id    BIGINT UNSIGNED  NOT NULL COMMENT '댓글이 작성된 게시글 ID',
    member_id  BINARY(16)       NOT NULL COMMENT '작성한 회원 ID',
    content    TEXT             NOT NULL COMMENT '댓글 내용',
    like_count INTEGER UNSIGNED NOT NULL DEFAULT 0 COMMENT '추천수',
    created_at DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    deleted_at DATETIME         NULL COMMENT '삭제 일시',
    CONSTRAINT PK_comments PRIMARY KEY (comment_id),
    CONSTRAINT FK_comments_posts FOREIGN KEY (post_id) REFERENCES posts (post_id),
    CONSTRAINT FK_comments_members FOREIGN KEY (member_id) REFERENCES members (member_id)
) COMMENT '댓글 테이블';

CREATE TABLE comment_likes
(
    member_id  BINARY(16)      NOT NULL COMMENT '좋아요를 누른 회원 ID',
    comment_id BIGINT UNSIGNED NOT NULL COMMENT '좋아요를 받은 댓글 ID',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    CONSTRAINT PK_comment_likes PRIMARY KEY (member_id, comment_id),
    CONSTRAINT FK_comment_likes_members FOREIGN KEY (member_id) REFERENCES members (member_id),
    CONSTRAINT FK_comment_likes_comments FOREIGN KEY (comment_id) REFERENCES comments (comment_id)
) COMMENT '댓글 좋아요 테이블';
