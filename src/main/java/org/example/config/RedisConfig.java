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
@Bean(name = "objectRedisTemplate")
public   RedisTemplate<String, Object> ObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
    @Bean
    public RedisTemplate<String, Integer> integerRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisSerializer<Integer>() {
            @Override
            public byte[] serialize(Integer value) {
                return value == null ? null : value.toString().getBytes();
            }

            @Override
            public Integer deserialize(byte[] bytes) {
                return bytes == null ? null : Integer.parseInt(new String(bytes));
            }
        });
        return template;
    }
   // @Bean(name = "byteArrayRedisTemplate")
    @Bean
      public RedisTemplate<String, byte[]> byteArrayRedisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, byte[]> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new RedisSerializer<byte[]>() {
                @Override
                public byte[] serialize(byte[] value) {
                    return value;
                }

                @Override
                public byte[] deserialize(byte[] bytes) {
                    return bytes;
                }
            });
            return template;
    }
}
