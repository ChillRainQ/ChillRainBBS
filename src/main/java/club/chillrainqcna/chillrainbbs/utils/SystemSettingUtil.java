package club.chillrainqcna.chillrainbbs.utils;

import club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ChillRain 2023 04 16
 */
public class SystemSettingUtil {
    private static final String KEY_SYS = "sys_setting";
    private static final Map<String, SystemSetting> CACHE_DATA = new ConcurrentHashMap<>();
    public static SystemSetting getSystemSetting(){
        return CACHE_DATA.get(KEY_SYS);
    }
    public static void refresh(SystemSetting setting){
        CACHE_DATA.put(KEY_SYS, setting);
    }
}
