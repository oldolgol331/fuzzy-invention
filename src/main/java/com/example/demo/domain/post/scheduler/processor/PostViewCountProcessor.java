package com.example.demo.domain.post.scheduler.processor;

import com.example.demo.infra.redis.dao.RedisRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
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

    private final JdbcTemplate              jdbcTemplate;
    private final RedisRepository           redisRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public PostViewCountProcessor(
            @Qualifier("dataJdbcTemplate") final JdbcTemplate jdbcTemplate,
            final RedisRepository redisRepository,
            final ElasticsearchRestTemplate elasticsearchRestTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisRepository = redisRepository;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Transactional
    public void processChunk(final List<String> chunkKeys) {
        if (chunkKeys.isEmpty()) return;

        List<String> counts = redisRepository.multiGetValues(chunkKeys, String.class);

        Map<Long, Long> postIdToViewCountMap = new HashMap<>();
        IntStream.range(0, chunkKeys.size()).forEach(i -> {
            String key      = chunkKeys.get(i);
            String countStr = counts.get(i);
            if (countStr != null) {
                long postId = Long.parseLong(key.split(":")[3]);
                long count  = Long.parseLong(countStr);
                postIdToViewCountMap.put(postId, count);
            }
        });

        if (postIdToViewCountMap.isEmpty()) {
            redisRepository.deleteData(chunkKeys);
            return;
        }

        List<Object[]> batchArgs = postIdToViewCountMap.entrySet()
                                                       .stream()
                                                       .map(entry -> new Object[]{entry.getValue(), entry.getKey()})
                                                       .collect(Collectors.toList());
        jdbcTemplate.batchUpdate("UPDATE posts SET view_count = view_count + ? WHERE post_id = ?", batchArgs);

        registerAfterCommitTasks(chunkKeys, postIdToViewCountMap);
    }

    // ========================= Private Methods =========================

    private void registerAfterCommitTasks(final List<String> keysToDelete, final Map<Long, Long> countsToUpdate) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                updateElasticsearch(countsToUpdate);
                redisRepository.deleteData(keysToDelete);
            }
        });
    }

    private void updateElasticsearch(final Map<Long, Long> countsToUpdate) {
        if (countsToUpdate.isEmpty()) return;

        List<UpdateQuery> updateQueries = countsToUpdate.entrySet()
                                                        .stream()
                                                        .map(entry -> {
                                                            long postId             = entry.getKey();
                                                            long viewCountIncrement = entry.getValue();

                                                            String script = "ctx._source.viewCount += params.count";
                                                            Map<String, Object> params = Collections.singletonMap(
                                                                    "count", viewCountIncrement
                                                            );

                                                            return UpdateQuery.builder(String.valueOf(postId))
                                                                              .withScript(script)
                                                                              .withParams(params)
                                                                              .build();
                                                        })
                                                        .collect(Collectors.toList());

        elasticsearchRestTemplate.bulkUpdate(updateQueries, IndexCoordinates.of("posts"));
    }

}
