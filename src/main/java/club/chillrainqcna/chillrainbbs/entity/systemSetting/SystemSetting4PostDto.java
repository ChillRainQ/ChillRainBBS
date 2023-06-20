package club.chillrainqcna.chillrainbbs.entity.systemSetting;

import lombok.Data;

/**
 * 发帖设置
 * @author ChillRain 2023 04 16
 */
@Data
public class SystemSetting4PostDto {
    /**
     * 发帖积分
     */
    private Integer postIntegeral;
    /**
     * 单日最大发帖数量
     */
    private Integer postDayCountThreshold;
    /**
     * 单日最大图片上传数量
     */
    private Integer dayImageUploadCount;
    /**
     * 附件大小
     */
    private Integer attachmentSize;
}
