package com.example.demo.domain.post.service;

import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.infra.elasticsearch.post.dao.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostCacheServiceImpl
 * Author      : oldolgol331
 * Date        : 25. 9. 1.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 1.     oldolgol331          Initial creation
 */
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    private final PostSearchRepository postSearchRepository;

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param keyword  - 검색어
     * @param pageable - 페이징 정보
     * @return 게시글 페이징 목록 응답 DTO
     */
    @Cacheable(
            value = "post-list",
            key = "#pageable.pageNumber",
            condition = "(#keyword == null or #keyword.trim().empty) and #pageable.pageNumber < 10"
    )
    @Override
    public Page<PostListResponse> getPosts(final String keyword, final Pageable pageable) {
        return postSearchRepository.getPosts(keyword, pageable);
    }

    @CacheEvict(value = "post-list", allEntries = true)
    @Override
    public void evictPostListCache() {
    }

}
