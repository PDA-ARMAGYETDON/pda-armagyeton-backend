package com.example.chatting.config;

import com.example.chatting.domain.ChatMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
//        redisConfiguration.setHostName("3.38.8.79");
//        redisConfiguration.setPort(6379);
//        return new LettuceConnectionFactory(redisConfiguration);
//    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate() {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, ChatMessage> redisTemplateForMessage(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        // Value serializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateForGenericObject(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public ValueOperations<String, ChatMessage> valueOperations(RedisTemplate<String, ChatMessage> redisTemplate) {
        return redisTemplate.opsForValue();
    }

}
