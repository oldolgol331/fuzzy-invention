package com.example.demo.domain.comment.service;

import com.example.demo.domain.comment.dto.CommentRequest.CommentCreateRequest;
import com.example.demo.domain.comment.dto.CommentRequest.CommentUpdateRequest;
import com.example.demo.domain.comment.dto.CommentResponse.CommentListResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PackageName : com.example.demo.domain.comment.service
 * FileName    : CommentService
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
public interface CommentService {

    void createComment(Long postId, UUID writerId, CommentCreateRequest request);

    Page<CommentListResponse> getComments(Long postId, UUID writerId, Pageable pageable);

    void updateComment(Long commentId, Long postId, UUID writerId, CommentUpdateRequest request);

    void deleteComment(Long commentId, Long postId, UUID writerId);

    void likeComment(Long commentId, Long postId, UUID memberId);

}
