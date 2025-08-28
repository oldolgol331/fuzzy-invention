package com.example.demo.infra.elasticsearch.post.dao;

import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PackageName : com.example.demo.infra.elasticsearch.post.dao
 * FileName    : PostSearchRepositoryCustom
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
public interface PostSearchRepositoryCustom {

    Page<PostListResponse> getPosts(String keyword, Pageable pageable);

}
