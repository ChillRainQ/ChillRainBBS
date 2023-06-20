package club.chillrainqcna.chillrainbbs.entity.systemSetting;

import lombok.Data;

/**
 * 点赞设置
 * @author ChillRain 2023 04 16
 */
@Data
public class SystemSetting4LikeDto {
    /**
     * 点赞阈值
     */
    private Integer likeDayCountThreshold;
}
