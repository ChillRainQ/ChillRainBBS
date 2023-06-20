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
public class EmailCode {
    private String email;

    private String code;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 0:未使用  1:已使用
     */
    private Integer status;

    @Override
    public String toString() {
        return "EmailCode{" +
                "email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }
}
