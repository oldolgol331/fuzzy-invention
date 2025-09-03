package com.example.demo.domain.post.service;

import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostCacheService
 * Author      : oldolgol331
 * Date        : 25. 9. 1.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 1.     oldolgol331          Initial creation
 */
public interface PostCacheService {

    List<PostListResponse> getPosts(String keyword, Pageable pageable);

    long getTotalCount(String keyword);

    void evictPostListCache();

}
