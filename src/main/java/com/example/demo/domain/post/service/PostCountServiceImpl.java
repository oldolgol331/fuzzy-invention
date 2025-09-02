package com.example.demo.domain.post.service;

import static com.example.demo.domain.post.constant.PostConst.POST_VIEW_COUNT_KEY_PREFIX;
import static com.example.demo.domain.post.constant.PostConst.POST_VIEW_KEY_PREFIX;
import static com.example.demo.domain.post.constant.PostConst.POST_VIEW_LOG_KEY_PREFIX;
import static lombok.AccessLevel.PRIVATE;

import com.example.demo.infra.elasticsearch.post.dao.PostSearchRepository;
import com.example.demo.infra.redis.dao.RedisRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostCountServiceImpl
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
public class PostCountServiceImpl implements PostCountService {

    private static final Duration POST_VIEW_LOG_TTL   = Duration.ofDays(1);
    private static final Duration POST_VIEW_COUNT_TTL = Duration.ofHours(1);

    private final PostSearchRepository postSearchRepository;
    private final RedisRepository      redisRepository;

    /**
     * Redis에서 게시글 ID를 기준으로 조회수, 좋아요 수, 댓글 수를 포함한 DTO를 조회합니다.
     *
     * @param postIds - 게시글 ID 목록
     * @return 게시글 조회수, 좋아요 수, 댓글 수를 포함한 DTO
     */
    @Override
    public Map<Long, PostCountDto> getViewCounts(final List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();

        List<String> viewKeys = postIds.stream()
                                       .map(id -> String.format(POST_VIEW_KEY_PREFIX, id))
                                       .collect(Collectors.toList());

        List<Long>              viewCountsFromRedis       = redisRepository.multiGetValues(viewKeys, Long.class);
        Map<Long, PostCountDto> likeAndCommentCountFromEs = postSearchRepository.findCountsById(postIds);

        return IntStream.range(0, postIds.size())
                        .boxed()
                        .collect(
                                Collectors.toMap(
                                        postIds::get,
                                        i -> {
                                            PostCountDto esDto = likeAndCommentCountFromEs.get(postIds.get(i));
                                            return PostCountDto.of(
                                                    Optional.ofNullable(viewCountsFromRedis.get(i)).orElse(0L),
                                                    (esDto != null) ? esDto.getLikeCount() : 0,
                                                    (esDto != null) ? esDto.getCommentCount() : 0);
                                        }
                                )
                        );
    }

    /**
     * 게시글 조회수 증가를 Redis에 기록합니다. 클라이언트 IP를 사용해서 첫 조회일 경우 조회수 증가하고, 조회 시점에서 24시간 동안 같은 게시글 ID에
     * 대해 조회수가 증가하지 않습니다.
     *
     * @param postId   - 게시글 ID
     * @param clientIp - 클라이언트 IP
     */
    @Override
    public void incrementViewCount(final Long postId, final String clientIp) {
        boolean isFirstView = redisRepository.setValueIfAbsent(
                String.format(POST_VIEW_LOG_KEY_PREFIX, postId, clientIp), 1, POST_VIEW_LOG_TTL
        );

        if (isFirstView) redisRepository.increment(String.format(POST_VIEW_COUNT_KEY_PREFIX, postId));
    }

    /**
     * 게시글 조회수를 Redis에 기록합니다. Redis에 존재하지 않을 경우 Elasticsearch에서 값을 찾아 Redis에 기록하며, Redis에 존재할 경우
     * 기존 값에 조회수를 1만큼 증가시킵니다.
     *
     * @param postId - 게시글 ID
     */
    @Override
    public void setViewCountToRedis(final Long postId) {
        String viewKey = String.format(POST_VIEW_KEY_PREFIX, postId);

        Optional<Long> opViewCountFromRedis = redisRepository.getValue(viewKey, Long.class);

        if (opViewCountFromRedis.isPresent()) {
            redisRepository.increment(viewKey);
            redisRepository.expire(viewKey, POST_VIEW_COUNT_TTL);
        } else
            redisRepository.setValue(
                    viewKey, postSearchRepository.findViewCountById(postId).orElse(1L), POST_VIEW_COUNT_TTL
            );
    }

    @Getter
    @AllArgsConstructor(access = PRIVATE)
    @Builder(access = PRIVATE)
    public static class PostCountDto {

        @Setter
        private long viewCount;
        private int  likeCount;
        private int  commentCount;

        public static PostCountDto of(final long viewCount, final int likeCount, final int commentCount) {
            return PostCountDto.builder().viewCount(viewCount).likeCount(likeCount).commentCount(commentCount).build();
        }

    }

}
