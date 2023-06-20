package club.chillrainqcna.chillrainbbs.entity.systemSetting;

import lombok.Data;

/**
 * 评论设置
 * @author ChillRain 2023 04 16
 */
@Data
public class SystemSetting4CommentDto {
    /**
     * 评论积分
     */
    private Integer commentIntegeral;
    /**
     * 评论数量阈值
     */
    private Integer commentDayCountThreshold;
    /**
     * 评论开关
     */
    private Boolean commentOpen;
}
