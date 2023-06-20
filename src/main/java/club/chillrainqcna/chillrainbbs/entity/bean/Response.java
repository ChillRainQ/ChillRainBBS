package club.chillrainqcna.chillrainbbs.entity.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChillRain 2023 04 15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;
}
