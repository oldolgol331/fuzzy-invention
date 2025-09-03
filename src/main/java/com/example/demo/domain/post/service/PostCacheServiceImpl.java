package com.example.demo.domain.post.service;

import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.infra.elasticsearch.post.dao.PostSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            value = "posts",
            //key = "#pageable.pageNumber",
            keyGenerator = "listKeyGenerator",
            condition = "(#keyword == null or #keyword.trim().empty)" +
                        " and #pageable.pageNumber < 10" +
                        " and #root.target.isDefaultSort(#pageable)"
    )
    @Override
    public List<PostListResponse> getPosts(final String keyword, final Pageable pageable) {
        return postSearchRepository.getPosts(keyword, pageable).getContent();
    }

    /**
     * 게시글 목록 전체 수를 조회합니다.
     *
     * @param keyword - 검색어
     * @return 전체 게시글 수
     */
    @Override
    public long getTotalCount(final String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return postSearchRepository.count();
        return postSearchRepository.countByKeyword(keyword);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Override
    public void evictPostListCache() {
    }

    /**
     * Pageable 객체의 정렬 조건이 기본값인지 확인합니다.
     *
     * @param pageable - 페이징 정보
     * @return 정렬 조건이 기본값인 경우 true, 그렇지 않으면 false
     */
    public boolean isDefaultSort(final Pageable pageable) {
        Sort sort = pageable.getSort();

        if (sort.isUnsorted() || sort.stream().count() != 1) return false;

        Sort.Order order = sort.getOrderFor("createdAt");
        return order != null && order.isDescending();
    }

}
