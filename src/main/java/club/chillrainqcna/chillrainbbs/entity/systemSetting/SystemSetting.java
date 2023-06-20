package club.chillrainqcna.chillrainbbs.entity.systemSetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统设置
 * @author ChillRain 2023 04 16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Accessors(chain = true)
public class SystemSetting {
    private SystemSetting4AuditDto audit;
    private SystemSetting4CommentDto comment;
    private SystemSetting4EmailDto email;
    private SystemSetting4LikeDto like;
    private SystemSetting4PostDto post;
    private SystemSetting4Register register;
}
