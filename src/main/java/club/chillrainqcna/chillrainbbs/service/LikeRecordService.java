package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.enums.DoLikeTypeEnum;

public interface LikeRecordService {
    void doLike(String objectId, String userId, String nickName, DoLikeTypeEnum typeEnum);
}
