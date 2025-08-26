package com.example.demo.domain.post.service;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostViewCountService
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
public interface PostViewCountService {

    void incrementViewCount(Long postId, String clientIp);

}
