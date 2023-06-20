package club.chillrainqcna.chillrainbbs.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author ChillRain 2023 05 03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {
    private String userId;


    private String nickName;



    private Integer sex;

    /**
     * 个人描述
     */
    private String personDescription;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date joinTime;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd ")
    private Date lastLoginTime;



    private String lastLoginIpAddress;

    private Integer postCount;
    private Integer likeCount;

    /**
     * 当前积分
     */
    private Integer currentIntegral;

    /**
     * 0:禁用 1:正常
     */
    private Integer status;
}
