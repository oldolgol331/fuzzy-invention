package com.example.demo.domain.comment.dao;

import com.example.demo.domain.comment.dto.CommentResponse.CommentListResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PackageName : com.example.demo.domain.comment.dao
 * FileName    : CommentRepositoryCustom
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
public interface CommentRepositoryCustom {

    Page<CommentListResponse> getComments(Long postId, UUID writerId, Pageable pageable);

}
