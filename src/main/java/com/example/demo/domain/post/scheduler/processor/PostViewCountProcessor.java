package com.example.demo.domain.post.scheduler.processor;

import com.example.demo.infra.redis.dao.RedisRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * PackageName : com.example.demo.domain.post.scheduler.processor
 * FileName    : PostViewCountProcessor
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@Component
public class PostViewCountProcessor {

    private final JdbcTemplate    jdbcTemplate;
    private final RedisRepository redisRepository;

    public PostViewCountProcessor(
            @Qualifier("dataJdbcTemplate") final JdbcTemplate jdbcTemplate, final RedisRepository redisRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisRepository = redisRepository;
    }

    @Transactional
    public void processChunk(final List<String> chunkKeys) {
        if (chunkKeys.isEmpty()) return;

        List<String> counts = redisRepository.multiGetValues(chunkKeys, String.class);
        List<Object[]> batchArgs = IntStream.range(0, chunkKeys.size())
                                            .mapToObj(i -> {
                                                String key      = chunkKeys.get(i);
                                                String countStr = counts.get(i);
                                                if (countStr == null) return null;
                                                long postId = Long.parseLong(key.split(":")[3]);
                                                long count  = Long.parseLong(countStr);
                                                return new Object[]{count, postId};
                                            })
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList());

        if (batchArgs.isEmpty()) {
            deleteRedisKeysAfterCommit(chunkKeys);
            return;
        }

        jdbcTemplate.batchUpdate("UPDATE posts SET view_count = view_count + ? WHERE post_id = ?", batchArgs);

        deleteRedisKeysAfterCommit(chunkKeys);
    }

    private void deleteRedisKeysAfterCommit(final List<String> keysToDelete) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisRepository.deleteData(keysToDelete);
            }
        });
    }

}
