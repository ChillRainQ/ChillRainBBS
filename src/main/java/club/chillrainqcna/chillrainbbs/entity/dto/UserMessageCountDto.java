package club.chillrainqcna.chillrainbbs.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChillRain 2023 05 03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageCountDto {
    private Long total = 0L;
    public Long sys = 0L;
    public Long reply = 0L;
    private Long likePost = 0L;
    private Long likeComment = 0L;
    private Long downloadAttachment = 0L;
}
