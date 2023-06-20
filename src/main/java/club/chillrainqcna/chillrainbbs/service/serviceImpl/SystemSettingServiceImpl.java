package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.entity.bean.SysSetting;
import club.chillrainqcna.chillrainbbs.entity.enums.SysSettingEnum;
import club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting;
import club.chillrainqcna.chillrainbbs.mappers.SystemSettingMapper;
import club.chillrainqcna.chillrainbbs.service.SystemSettingService;
import club.chillrainqcna.chillrainbbs.utils.JsonUtil;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import club.chillrainqcna.chillrainbbs.utils.SystemSettingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ChillRain 2023 04 16
 */
@Service
public class SystemSettingServiceImpl implements SystemSettingService {
    @Resource
    private SystemSettingMapper systemSettingMapper;
    @Override
    public SystemSetting refreshCache() {
        System.out.println("=============>  refreshing----");
        try{
            SystemSetting systemSetting = new SystemSetting();
            List<SysSetting> sysSettings = systemSettingMapper.selectList(null);
            for (SysSetting sysSetting : sysSettings) {
                String content = sysSetting.getJsonContent();//Json的内容
                if(NotNullUtil.isEmpty(content)){
                    continue;
                }
                String code = sysSetting.getCode();//获取设置的类型
                SysSettingEnum set = SysSettingEnum.getByCode(code);//根据code获取对应的枚举
                PropertyDescriptor pd = new PropertyDescriptor(set.getPropName(), SystemSetting.class);
                Method method = pd.getWriteMethod();
                Class clazz = Class.forName(set.getClassName());
                method.invoke(systemSetting, JsonUtil.json2object(content, clazz));
            }
            SystemSettingUtil.refresh(systemSetting);
            System.out.println("初始化完成");
            return systemSetting;
        }catch (Exception e){
            System.out.println("初始化失败");

        }


        return null;
    }

    /**
     * 反射把各个值转为json
     * @param systemSetting
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSystemSetting(SystemSetting systemSetting) {
        try{
            Class<? extends SystemSetting> clazz = systemSetting.getClass();
            for(SysSettingEnum item : SysSettingEnum.values()){
                PropertyDescriptor pd = new PropertyDescriptor(item.getPropName(), clazz);
                Method method = pd.getReadMethod();
                Object obj = method.invoke(systemSetting);
                String objJson = JsonUtil.object2Json(obj);
                SysSetting sysSetting = new SysSetting();
                sysSetting.setCode(item.getCode())
                        .setJsonContent(objJson);
                systemSettingMapper.updateOrInsert(sysSetting);
                SystemSettingUtil.refresh(systemSetting);
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
