package com.example.demo.domain.post.scheduler.processor;

import com.example.demo.infra.redis.dao.RedisRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

        List<Long> updatedPostIds = new ArrayList<>();
        List<Object[]> batchArgs = chunkKeys.stream()
                                            .map(key -> {
                                                long postId = Long.parseLong(key.split(":")[3]);
                                                updatedPostIds.add(postId);
                                                Optional<String> opCountStr = redisRepository.getValue(
                                                        key, String.class
                                                );
                                                long count = 0;
                                                if (opCountStr.isPresent()) count = Long.parseLong(opCountStr.get());
                                                return new Object[]{count, postId};
                                            })
                                            .collect(Collectors.toList());
        jdbcTemplate.batchUpdate("UPDATE posts SET view_count = view_count + ? WHERE post_id = ?", batchArgs);

        redisRepository.deleteData(chunkKeys);
    }

}
