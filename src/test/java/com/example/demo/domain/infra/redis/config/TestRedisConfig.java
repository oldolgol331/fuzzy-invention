package com.example.demo.domain.infra.redis.config;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import java.io.IOException;
import java.net.ServerSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

/**
 * PackageName : com.example.demo.domain.infra.redis.config
 * FileName    : TestRedisConfig
 * Author      : oldolgol331
 * Date        : 25. 9. 4.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 4.     oldolgol331          Initial creation
 */
@Profile("test")
@TestConfiguration
@Slf4j
public class TestRedisConfig {

    private int         redisPort;
    private RedisServer redisServer;

    @Bean(destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        redisPort = findAvailablePort();
        log.info("Found available port for Embedded Redis: {}", redisPort);

        redisServer = new RedisServer(redisPort);

        try {
            redisServer.start();
            return redisServer;
        } catch (Exception e) {
            throw new RuntimeException("Embedded Redis server failed to start", e);
        }
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(final RedisServer redisServer) {
        log.info("Connecting to Embedded Redis on port: {}", redisPort);
        return new LettuceConnectionFactory("localhost", redisPort);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType(Object.class)
                                                                    .build();
        objectMapper.activateDefaultTyping(ptv, NON_FINAL);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return redisTemplate;
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

}
