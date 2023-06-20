package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author ChillRain 2023 04 16
 */
@Repository
public interface UserMessageMapper extends BaseMapper<UserMessage> {
    @MapKey("userId")
    List<Map> selectUserMessageCount(@Param("userId") String userId);
    void updateMessageStatusBatch(@Param("receivedUserId")String receivedUserId, @Param("messageType") Integer messageType, @Param("status")Integer status);

}
