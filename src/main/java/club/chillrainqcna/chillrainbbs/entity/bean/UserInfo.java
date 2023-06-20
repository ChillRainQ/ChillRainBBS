package club.chillrainqcna.chillrainbbs.entity.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author ChillRain 2023 04 16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String userId;


    private String nickName;


    private String email;


    private String password;


    private Integer sex;

    /**
     * 个人描述
     */
    private String personDescription;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;


    private String lastLoginIp;

    private String lastLoginIpAddress;

    /**
     * 积分
     */
    private Integer totalIntegral;

    /**
     * 当前积分
     */
    private Integer currentIntegral;

    /**
     * 0:禁用 1:正常
     */
    private Integer status;

}
