package com.example.demo.domain.comment.service;

import static com.example.demo.common.response.ErrorCode.COMMENT_LIKE_CANNOT;
import static com.example.demo.common.response.ErrorCode.COMMENT_NOT_FOUND;
import static com.example.demo.common.response.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.demo.common.response.ErrorCode.POST_NOT_FOUND;
import static com.example.demo.domain.member.model.MemberStatus.ACTIVE;

import com.example.demo.common.error.CustomException;
import com.example.demo.domain.comment.dao.CommentLikeRepository;
import com.example.demo.domain.comment.dao.CommentRepository;
import com.example.demo.domain.comment.dto.CommentRequest.CommentCreateRequest;
import com.example.demo.domain.comment.dto.CommentRequest.CommentUpdateRequest;
import com.example.demo.domain.comment.dto.CommentResponse.CommentListResponse;
import com.example.demo.domain.comment.model.Comment;
import com.example.demo.domain.comment.model.CommentLike;
import com.example.demo.domain.comment.model.CommentLikeId;
import com.example.demo.domain.member.dao.MemberRepository;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.post.dao.PostRepository;
import com.example.demo.domain.post.model.Post;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.example.demo.domain.comment.service
 * FileName    : CommentServiceImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository     commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository        postRepository;
    private final MemberRepository      memberRepository;

    /**
     * 새로운 댓글을 생성합니다.
     *
     * @param postId   - 게시글 ID
     * @param writerId - 작성자 ID
     * @param request  - 댓글 생성 요청 DTO
     */
    @Transactional
    @Override
    public void createComment(
            final Long postId, final UUID writerId, final CommentCreateRequest request
    ) {
        Member writer = memberRepository.findByIdAndMemberStatus(writerId, ACTIVE)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                                  .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        Comment savedComment = commentRepository.save(Comment.of(writer, post, request.getContent()));
        postRepository.updateCommentCount(savedComment.getPost().getId(), 1);
    }

    /**
     * 댓글 목록을 조회합니다.
     *
     * @param postId   - 게시글 ID
     * @param pageable - 페이징 정보
     * @return 댓글 페이징 목록 응답 DTO
     */
    @Override
    public Page<CommentListResponse> getComments(final Long postId, final UUID writerId, final Pageable pageable) {
        return commentRepository.getComments(postId, writerId, pageable);
    }

    /**
     * 댓글을 수정합니다.
     *
     * @param commentId - 댓글 ID
     * @param postId    - 게시글 ID
     * @param writerId  - 작성자 ID
     * @param request   - 댓글 수정 요청 DTO
     */
    @Transactional
    @Override
    public void updateComment(
            final Long commentId, final Long postId, final UUID writerId, final CommentUpdateRequest request
    ) {
        if (!memberRepository.existsByIdAndMemberStatus(writerId, ACTIVE)) throw new CustomException(MEMBER_NOT_FOUND);
        if (!postRepository.existsByIdAndIsDeletedFalse(postId)) throw new CustomException(POST_NOT_FOUND);

        Comment comment = commentRepository.findByIdAndWriterIdAndPostIdAndIsDeletedFalse(commentId, writerId, postId)
                                           .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        comment.setContent(request.getContent());
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param commentId - 댓글 ID
     * @param postId    - 게시글 ID
     * @param writerId  - 작성자 ID
     */
    @Transactional
    @Override
    public void deleteComment(final Long commentId, final Long postId, final UUID writerId) {
        if (!memberRepository.existsByIdAndMemberStatus(writerId, ACTIVE)) throw new CustomException(MEMBER_NOT_FOUND);
        if (!postRepository.existsByIdAndIsDeletedFalse(postId)) throw new CustomException(POST_NOT_FOUND);

        Comment comment = commentRepository.findByIdAndWriterIdAndPostIdAndIsDeletedFalse(commentId, writerId, postId)
                                           .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        comment.delete();
        postRepository.updateCommentCount(comment.getPost().getId(), -1);
    }

    /**
     * 댓글에 좋아요를 추가하거나 추가된 좋아요를 취소합니다.
     *
     * @param commentId - 댓글 ID
     * @param postId    - 게시글 ID
     * @param memberId  - 회원 ID
     */
    @Transactional
    @Override
    public void likeComment(final Long commentId, final Long postId, final UUID memberId) {
        Member member = memberRepository.findByIdAndMemberStatus(memberId, ACTIVE)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                                  .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                                           .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        if (comment.getWriter().getId().equals(memberId)) throw new CustomException(COMMENT_LIKE_CANNOT);

        CommentLikeId commentLikeId = CommentLikeId.builder().memberId(memberId).commentId(commentId).build();
        if (commentLikeRepository.existsById(commentLikeId)) commentLikeRepository.deleteById(commentLikeId);
        else commentLikeRepository.save(CommentLike.of(member, comment));
    }

}
