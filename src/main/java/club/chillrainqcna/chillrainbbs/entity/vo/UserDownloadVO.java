package club.chillrainqcna.chillrainbbs.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChillRain 2023 04 22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDownloadVO {
    private Integer userIntegral;
    private Boolean haveDownload;
}
