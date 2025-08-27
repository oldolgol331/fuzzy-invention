package com.example.demo.domain.comment.dao;

import com.example.demo.common.config.annotation.DataDBJpaRepositoryMarker;
import com.example.demo.domain.comment.model.Comment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.domain.comment.dao
 * FileName    : CommentRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@DataDBJpaRepositoryMarker
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    Optional<Comment> findByIdAndWriterIdAndPostIdAndIsDeletedFalse(Long id, UUID writerId, Long postId);

}
