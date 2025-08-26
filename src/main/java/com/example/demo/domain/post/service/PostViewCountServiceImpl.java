package com.example.demo.domain.post.service;

import static com.example.demo.domain.post.constant.PostConst.POST_VIEW_COUNT_KEY_PREFIX;
import static com.example.demo.domain.post.constant.PostConst.POST_VIEW_LOG_KEY_PREFIX;

import com.example.demo.infra.redis.dao.RedisRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostViewCountServiceImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@Service
@RequiredArgsConstructor
public class PostViewCountServiceImpl implements PostViewCountService {

    private static final Duration POST_VIEW_LOG_TTL = Duration.ofDays(1);

    private final RedisRepository redisRepository;

    /**
     * 게시글 조회수 증가를 Redis에 기록합니다. 클라이언트 IP를 사용해서 첫 조회일 경우 조회수 증가하고, 조회 시점에서 24시간 동안 같은 게시글 ID에
     * 대해 조회수가 증가하지 않습니다.
     *
     * @param postId   - 게시글 ID
     * @param clientIp - 클라이언트 IP
     */
    @Override
    public void incrementViewCount(final Long postId, final String clientIp) {
        String  postViewLogKey = String.format(POST_VIEW_LOG_KEY_PREFIX, postId, clientIp);
        boolean isFirstView    = redisRepository.setValueIfAbsent(postViewLogKey, 1, POST_VIEW_LOG_TTL);

        if (isFirstView) {
            String postViewCountKey = String.format(POST_VIEW_COUNT_KEY_PREFIX, postId);
            redisRepository.increment(postViewCountKey);
        }
    }

}
