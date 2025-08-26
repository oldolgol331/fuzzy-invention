package com.example.demo.domain.post.scheduler;

import static com.example.demo.domain.post.constant.PostConst.POST_VIEW_COUNT_PATTERN;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.example.demo.domain.post.scheduler.processor.PostViewCountProcessor;
import com.example.demo.infra.redis.dao.RedisRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PackageName : com.example.demo.domain.post.scheduler
 * FileName    : PostViewCountSyncScheduler
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@Component
@RequiredArgsConstructor
public class PostViewCountSyncScheduler {

    private static final int CHUNK_SIZE = 1000;

    private final PostViewCountProcessor postViewCountProcessor;
    private final RedisRepository        redisRepository;

    @Scheduled(fixedRate = 1, timeUnit = MINUTES)
    public void syncViewCount() {
        try (
                Cursor<String> cursor = redisRepository.scan(
                        ScanOptions.scanOptions().match(POST_VIEW_COUNT_PATTERN).count(CHUNK_SIZE).build()
                )
        ) {
            while (cursor.hasNext()) {
                List<String> chunkKeys = new ArrayList<>();
                for (int i = 0; i < CHUNK_SIZE && cursor.hasNext(); i++) chunkKeys.add(cursor.next());
                postViewCountProcessor.processChunk(chunkKeys);
            }
        }
    }

}
