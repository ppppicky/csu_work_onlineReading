package org.example.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

//    @Bean(name = "stringRedisTemplate")
//    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setConnectionFactory(connectionFactory);
//        return redisTemplate;
//    }

   // @Bean(name = "byteArrayRedisTemplate")
    @Bean
    public RedisTemplate<String, byte[]> byteArrayRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 设置 Key 和 HashKey 的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 和 HashValue 直接存储 byte[]，不进行额外序列化
        template.setValueSerializer(RedisSerializer.byteArray());
        template.setHashValueSerializer(RedisSerializer.byteArray());

        return template;
    }
}
