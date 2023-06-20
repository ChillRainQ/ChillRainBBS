package club.chillrainqcna.chillrainbbs.config.root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ChillRain 2023 04 16
 */
@Component
public class AppConfig {
    @Value("${project.folder}")
    private String programFolder;

    public String getProgramFolder() {
        return programFolder;
    }
}
