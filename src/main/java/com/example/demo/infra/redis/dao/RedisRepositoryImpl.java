package com.example.demo.infra.redis.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.example.demo.infra.redis.dao
 * FileName    : RedisRepositoryImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper                  objectMapper;

    @Override
    public void setValue(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public boolean setValueIfAbsent(final String key, final Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
    }

    @Override
    public boolean setValueIfPresent(final String key, final Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(key, value));
    }

    @Override
    public void setValue(final String key, final Object value, final Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public boolean setValueIfAbsent(final String key, final Object value, final Duration duration) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, duration));
    }

    @Override
    public boolean setValueIfPresent(final String key, final Object value, final Duration duration) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(key, value, duration));
    }

    @Override
    public void setValue(final String key, final Object value, final long timeout, final TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public boolean setValueIfAbsent(final String key, final Object value, final long timeout, final TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit));
    }

    @Override
    public boolean setValueIfPresent(final String key, final Object value, final long timeout, final TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(key, value, timeout, unit));
    }

    @Override
    public <T> Optional<T> getValue(final String key, final Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        return Optional.of(objectMapper.convertValue(value, type));
    }

    @Override
    public Long increment(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long increment(final String key, final long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Double increment(final String key, final double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public void setHash(final String key, final String hashKey, final Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public <T> Optional<T> getHash(final String key, final String hashKey, final Class<T> type) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        if (value == null) return Optional.empty();
        return Optional.of(objectMapper.convertValue(value, type));
    }

    @Override
    public Map<Object, Object> getAllHash(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Long deleteHash(final String key, final Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    @Override
    public Long addToSet(final String key, final Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Set<Object> getSetMembers(final String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        return members != null ? members : Collections.emptySet();
    }

    @Override
    public Long removeFromSet(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public Long leftPushToList(final String key, final Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public Long rightPushToList(final String key, final Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public <T> Optional<T> leftPopFromList(final String key, final Class<T> type) {
        Object value = redisTemplate.opsForList().leftPop(key);
        if (value == null) return Optional.empty();
        return Optional.of(objectMapper.convertValue(value, type));
    }

    @Override
    public <T> Optional<T> rightPopFromList(final String key, final Class<T> type) {
        Object value = redisTemplate.opsForList().rightPop(key);
        if (value == null) return Optional.empty();
        return Optional.of(objectMapper.convertValue(value, type));
    }

    @Override
    public List<Object> getListRange(final String key, final long start, final long end) {
        List<Object> range = redisTemplate.opsForList().range(key, start, end);
        return range != null ? range : Collections.emptyList();
    }

    @Override
    public Long getListSize(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public boolean addToZSet(final String key, final Object value, final double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
    }

    @Override
    public Set<Object> getZSetRangeByScore(final String key, final double minScore, final double maxScore) {
        Set<Object> range = redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
        return range != null ? range : Collections.emptySet();
    }

    @Override
    public Long getZSetRank(final String key, final Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    @Override
    public Long removeFromZSet(final String key, final Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    @Override
    public boolean deleteData(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public Long deleteData(final Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public boolean hasKey(final String key) {
        return Optional.ofNullable(redisTemplate.hasKey(key)).orElse(false);
    }

    @Override
    public Set<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public Cursor<String> scan(final ScanOptions options) {
        return redisTemplate.scan(options);
    }

    @Override
    public void expire(final String key, final Duration duration) {
        redisTemplate.expire(key, duration);
    }

    @Override
    public void expire(final String key, final long timeout, final TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

}
