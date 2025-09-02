package com.example.demo.domain.post.service;

import com.example.demo.domain.post.service.PostCountServiceImpl.PostCountDto;
import java.util.List;
import java.util.Map;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostCountService
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
public interface PostCountService {

    Map<Long, PostCountDto> getViewCounts(List<Long> postIds);

    void incrementViewCount(Long postId, String clientIp);

    void setViewCountToRedis(Long postId);

}
