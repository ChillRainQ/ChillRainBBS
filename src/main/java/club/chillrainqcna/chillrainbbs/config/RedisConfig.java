package club.chillrainqcna.chillrainbbs.config;

import club.chillrainqcna.chillrainbbs.config.root.AppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @author ChillRain 2023 04 16
 */
@Configuration
public class RedisConfig extends AppConfig {
    @Bean
    public JedisPool getRedisPool(){
        return new JedisPool();
    }
}
