package santaOps.santaLog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 일반 문자열로 직렬화 (RT:1, USER:1 등)
        template.setKeySerializer(new StringRedisSerializer());

        // Value는 JSON 직렬화 도구 사용 (UserCacheDto 객체를 JSON 문자열로 변환)
        // GenericJackson2JsonRedisSerializer는 객체의 클래스 정보까지 포함하여 저장
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash를 사용할 경우를 대비한 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}