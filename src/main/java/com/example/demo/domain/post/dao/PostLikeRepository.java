package com.example.demo.domain.post.dao;

import com.example.demo.common.config.annotation.DataDBJpaRepositoryMarker;
import com.example.demo.domain.post.model.PostLike;
import com.example.demo.domain.post.model.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.domain.post.dao
 * FileName    : PostLikeRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@DataDBJpaRepositoryMarker
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
}
