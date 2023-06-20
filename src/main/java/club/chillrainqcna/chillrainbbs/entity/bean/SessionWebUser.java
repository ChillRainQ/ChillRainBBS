package club.chillrainqcna.chillrainbbs.entity.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChillRain 2023 04 19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionWebUser {
    private String nikeName;
    private String province;
    private String userId;
    private String isAdmin;

}
