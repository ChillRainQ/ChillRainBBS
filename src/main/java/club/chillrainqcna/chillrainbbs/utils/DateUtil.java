package club.chillrainqcna.chillrainbbs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ChillRain 2023 05 01
 */
public class DateUtil {
    public static final String format(Date date, String format){
        return new SimpleDateFormat(format).format(date);
    }

}
