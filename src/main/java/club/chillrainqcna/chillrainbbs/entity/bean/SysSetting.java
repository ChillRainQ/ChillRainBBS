package club.chillrainqcna.chillrainbbs.entity.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ChillRain 2023 04 16
 */
@Data
@Accessors(chain = true)
public class SysSetting {
    private String code;
    private String jsonContent;
}
