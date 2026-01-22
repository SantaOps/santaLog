package santaOps.santaLog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "santaOps.santaLog.repository.jpa")
@EnableRedisRepositories(basePackages = "santaOps.santaLog.repository.redis")
public class DataRepositoryConfig {
}