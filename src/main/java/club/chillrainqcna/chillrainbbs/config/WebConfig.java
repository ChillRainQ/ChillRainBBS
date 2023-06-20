package club.chillrainqcna.chillrainbbs.config;


import club.chillrainqcna.chillrainbbs.config.root.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ChillRain 2023 04 16
 */
@Component
public class WebConfig extends AppConfig {
    @Value("${spring.mail.username:}")
    private String username;
    @Value("${admin.emails}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPass;
    @Value("${project.attachment.folder}")
    private String projectFolder;

    public String getAdminPass() {
        return adminPass;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getUsername() {
        return username;
    }
}
