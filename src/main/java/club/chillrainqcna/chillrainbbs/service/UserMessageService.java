package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.dto.UserMessageCountDto;

public interface UserMessageService {
    UserMessageCountDto getUserMessageCount(String userId);
    void readMessageByType(String receivedUserId, Integer messageType);
}
