package com.example.demo.domain.post.dao;

import com.example.demo.common.config.annotation.DataDBJpaRepositoryMarker;
import com.example.demo.domain.post.model.Post;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.domain.post.dao
 * FileName    : PostRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@DataDBJpaRepositoryMarker
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    Optional<Post> findByIdAndWriterIdAndIsDeletedFalse(Long id, UUID writerId);

    boolean existsByIdAndIsDeletedFalse(Long id);

}
