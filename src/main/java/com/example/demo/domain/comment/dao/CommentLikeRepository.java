package com.example.demo.domain.comment.dao;

import com.example.demo.common.config.annotation.DataDBJpaRepositoryMarker;
import com.example.demo.domain.comment.model.CommentLike;
import com.example.demo.domain.comment.model.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.domain.comment.dao
 * FileName    : CommentLikeRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@DataDBJpaRepositoryMarker
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
}
