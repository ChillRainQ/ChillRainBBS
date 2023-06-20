package club.chillrainqcna.chillrainbbs.utils;

/**
 * @author ChillRain 2023 04 16
 */
public class NotNullUtil {
    public static final boolean isEmpty(String obj){
        return (obj == null || "".equalsIgnoreCase(obj));
    }

}
