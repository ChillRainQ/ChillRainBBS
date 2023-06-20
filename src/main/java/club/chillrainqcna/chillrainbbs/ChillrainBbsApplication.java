package club.chillrainqcna.chillrainbbs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统入口
 */
@SpringBootApplication
@MapperScan("club.chillrainqcna.chillrainbbs.mappers")
public class ChillrainBbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChillrainBbsApplication.class, args);
    }

}
