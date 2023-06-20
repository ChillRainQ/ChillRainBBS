package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMapper extends BaseMapper<UserInfo> {
    Integer updateIntegral(String userId ,Integer integral);
}
