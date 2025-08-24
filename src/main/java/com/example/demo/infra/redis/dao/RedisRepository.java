package com.example.demo.infra.redis.dao;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

/**
 * PackageName : com.example.demo.infra.redis.dao
 * FileName    : RedisRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
public interface RedisRepository {

    void setValue(String key, Object value);

    boolean setValueIfAbsent(String key, Object value);

    boolean setValueIfPresent(String key, Object value);

    void setValue(String key, Object value, Duration duration);

    boolean setValueIfAbsent(String key, Object value, Duration duration);

    boolean setValueIfPresent(String key, Object value, Duration duration);

    void setValue(String key, Object value, long timeout, TimeUnit unit);

    boolean setValueIfAbsent(String key, Object value, long timeout, TimeUnit unit);

    boolean setValueIfPresent(String key, Object value, long timeout, TimeUnit unit);

    <T> Optional<T> getValue(String key, Class<T> type);

    Long increment(String key);

    Long increment(String key, long delta);

    Double increment(String key, double delta);

    void setHash(String key, String hashKey, Object value);

    <T> Optional<T> getHash(String key, String hashKey, Class<T> type);

    Map<Object, Object> getAllHash(String key);

    Long deleteHash(String key, Object... hashKeys);

    Long addToSet(String key, Object... values);

    Set<Object> getSetMembers(String key);

    Long removeFromSet(String key, Object... values);

    Long leftPushToList(String key, Object value);

    Long rightPushToList(String key, Object value);

    <T> Optional<T> leftPopFromList(String key, Class<T> type);

    <T> Optional<T> rightPopFromList(String key, Class<T> type);

    List<Object> getListRange(String key, long start, long end);

    Long getListSize(String key);

    boolean addToZSet(String key, Object value, double score);

    Set<Object> getZSetRangeByScore(String key, double minScore, double maxScore);

    Long getZSetRank(String key, Object value);

    Long removeFromZSet(String key, Object... values);

    boolean deleteData(String key);

    Long deleteData(Collection<String> keys);

    boolean hasKey(String key);

    Set<String> keys(String pattern);

    Cursor<String> scan(ScanOptions options);

    void expire(String key, Duration duration);

    void expire(String key, long timeout, TimeUnit unit);

}
