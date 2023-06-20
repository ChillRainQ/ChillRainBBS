package club.chillrainqcna.chillrainbbs.entity.systemSetting;

import lombok.Data;

/**
 * 审核设置
 * @author ChillRain 2023 04 16
 */
@Data
public class SystemSetting4AuditDto {
    /**
     * 评论审核
     */
    private Boolean commentAudit;
    /**
     * 帖子审核
     */
    private Boolean postAudit;
}
