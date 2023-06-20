package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.bean.SysSetting;
import club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting;
import com.baomidou.mybatisplus.extension.service.IService;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author ChillRain 2023 04 16
 */
public interface SystemSettingService{
    SystemSetting refreshCache() throws ClassNotFoundException, NoSuchMethodException, IntrospectionException, InvocationTargetException, IllegalAccessException;

    void saveSystemSetting(SystemSetting systemSetting);
}
