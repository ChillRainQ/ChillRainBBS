package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.SysSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface SystemSettingMapper extends BaseMapper<SysSetting> {
    Integer updateOrInsert(SysSetting sysSetting);
}
