package club.chillrainqcna.chillrainbbs;

import club.chillrainqcna.chillrainbbs.service.SystemSettingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统初始化
 * @author ChillRain 2023 04 16
 */
@Component
public class SystemInit implements ApplicationRunner {
    @Resource
    private SystemSettingService systemSettingService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        systemSettingService.refreshCache();
    }
}
