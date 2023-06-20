package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.entity.dto.UserMessageCountDto;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageStatusEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageTypeEunm;
import club.chillrainqcna.chillrainbbs.mappers.UserMessageMapper;
import club.chillrainqcna.chillrainbbs.service.UserMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author ChillRain 2023 05 03
 */
@Service
public class UserMessageServiceImpl implements UserMessageService {
    @Resource
    private UserMessageMapper userMessageMapper;
    @Override
    public UserMessageCountDto getUserMessageCount(String userId) {
        List<Map> mapList = userMessageMapper.selectUserMessageCount(userId);
        UserMessageCountDto userMessageCountDto = new UserMessageCountDto();
        Long totalCount = 0L;
        for(Map item : mapList){
            Integer type = (Integer)item.get("messageType");
            Long count = (Long) item.get("count");
            totalCount += count;
            MessageTypeEunm messageType = MessageTypeEunm.getByType(type);
            switch (messageType){
                case COMMENT:
                    userMessageCountDto.setReply(count);
                    break;
                case SYS:
                    userMessageCountDto.setSys(count);
                    break;
                case COMMENT_LIKE:
                    userMessageCountDto.setLikePost(count);
                    break;
                case ARTICLE_LIKE:
                    userMessageCountDto.setLikeComment(count);
                    break;
                case DOWNLOAD_ATTACHMENT:
                    userMessageCountDto.setDownloadAttachment(count);
                    break;
            }
        }
        userMessageCountDto.setTotal(totalCount);
        return userMessageCountDto;
    }

    @Override
    public void readMessageByType(String receivedUserId, Integer messageType) {
        userMessageMapper.updateMessageStatusBatch(receivedUserId, messageType, MessageStatusEnum.READ.getStatus());
    }
}
