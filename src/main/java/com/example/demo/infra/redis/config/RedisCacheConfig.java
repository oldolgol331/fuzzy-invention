package com.example.demo.infra.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * PackageName : com.example.demo.infra.redis.config
 * FileName    : RedisCacheConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 28.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 28.    oldolgol331          Initial creation
 */
@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public CacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper copiedObjectMapper = objectMapper.copy();

        copiedObjectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        RedisCacheConfiguration defaultConfig = createCacheConfiguration(Duration.ofHours(1), copiedObjectMapper);

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("posts", createCacheConfiguration(Duration.ofMinutes(10), copiedObjectMapper));
        cacheConfigurations.put("comments", createCacheConfiguration(Duration.ofMinutes(30), copiedObjectMapper));

        return RedisCacheManager.builder(redisConnectionFactory)
                                .cacheDefaults(defaultConfig)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .transactionAware()
                                .build();
    }

    private RedisCacheConfiguration createCacheConfiguration(final Duration ttl, final ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                                      .entryTtl(ttl)
                                      .serializeKeysWith(
                                              RedisSerializationContext.SerializationPair.fromSerializer(
                                                      new StringRedisSerializer()
                                              )
                                      )
                                      .serializeValuesWith(
                                              RedisSerializationContext.SerializationPair.fromSerializer(
                                                      new GenericJackson2JsonRedisSerializer(objectMapper)
                                              )
                                      );
    }

    @Bean
    public KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append("::");
            sb.append(method.getName());
            sb.append("::");
            sb.append(Arrays.deepToString(params));
            return sb.toString();
        };
    }

}
