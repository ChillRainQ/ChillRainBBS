package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticle;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumComment;
import club.chillrainqcna.chillrainbbs.entity.bean.LikeRecord;
import club.chillrainqcna.chillrainbbs.entity.bean.UserMessage;
import club.chillrainqcna.chillrainbbs.entity.enums.DoLikeTypeEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageStatusEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageTypeEunm;
import club.chillrainqcna.chillrainbbs.entity.enums.UpdateArticleTypeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.ForumArticleMapper;
import club.chillrainqcna.chillrainbbs.mappers.ForumCommentMapper;
import club.chillrainqcna.chillrainbbs.mappers.LikeRecordMapper;
import club.chillrainqcna.chillrainbbs.mappers.UserMessageMapper;
import club.chillrainqcna.chillrainbbs.service.ForumArticleService;
import club.chillrainqcna.chillrainbbs.service.LikeRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.rmi.server.ExportException;
import java.util.Date;

/**
 * @author ChillRain 2023 04 24
 */
@Service
public class LikeRecordServiceImpl implements LikeRecordService {
    @Resource
    private UserMessageMapper userMessageMapper;
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private LikeRecordMapper likeRecordMapper;
    @Resource
    private ForumCommentMapper forumCommentMapper;


    @Override
    public void doLike(String objectId, String userId, String nickName, DoLikeTypeEnum typeEnum) {
        UserMessage message = new UserMessage();
        message.setCreateTime(new Date());
        LikeRecord record = null;
        switch (typeEnum) {
            case ARTICLE_LIKE:
                ForumArticle article = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", objectId));
                if (article == null) {
                    throw new ChillRainBBSException("文章不存在");
                }
                record = articleLike(article, userId, nickName, typeEnum);
                message.setArticleId(objectId);
                message.setArticleTitle(article.getTitle());
                message.setMessageType(MessageTypeEunm.ARTICLE_LIKE.getType());
//                message.setSendUserId(userId);
                message.setCommentId(0);
                message.setReceivedUserId(article.getUserId());
                break;
            case COMMENT_LIKE:
                ForumComment comment = forumCommentMapper.selectOne(new QueryWrapper<ForumComment>().eq("comment_id", objectId));
                if(comment == null){
                    throw new ChillRainBBSException("消息不存在");
                }
                commentLike(comment, userId, typeEnum);//点赞动作
                ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", comment.getArticleId()));
                message.setArticleId(objectId);
                message.setArticleTitle(forumArticle.getTitle());
                message.setMessageType(MessageTypeEunm.ARTICLE_LIKE.getType());
                message.setCommentId(Integer.valueOf(objectId));
                message.setReceivedUserId(comment.getUserId());
                message.setMessageContent(comment.getContent());
                break;
        }
        message.setSendUserId(userId);
        message.setSendNickName(nickName);
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        if(record == null && !userId.equals(message.getReceivedUserId())){//没点赞  自赞
            userMessageMapper.insert(message);
        }

    }
    public LikeRecord commentLike(ForumComment comment, String userId, DoLikeTypeEnum typeEnum){
        QueryWrapper<LikeRecord> query = new QueryWrapper<>();
        query.eq("object_id", comment.getCommentId());
        query.eq("user_id", userId);
        query.eq("op_type", typeEnum.getType());
        LikeRecord record = likeRecordMapper.selectOne(query);
        if(record != null){//已点赞 清空并更新点赞信息
            likeRecordMapper.delete(query);
            comment.setGoodCount(comment.getGoodCount() - 1);
            forumCommentMapper.updateById(comment);
        }else{//没点过 创建记录 并更新信息
            LikeRecord record1 = new LikeRecord();
            record1.setObjectId(String.valueOf(comment.getCommentId()));
            record1.setUserId(userId);
            record1.setOpType(typeEnum.getType());
            record1.setCreateTime(new Date());
            record1.setAuthorUserId(comment.getUserId());
            likeRecordMapper.insert(record1);
            comment.setGoodCount(comment.getGoodCount() + 1);
            forumCommentMapper.updateById(comment);
        }
        return record;
    }
    @Transactional(rollbackFor = ExportException.class)
    public LikeRecord articleLike(ForumArticle article, String userId, String nickName, DoLikeTypeEnum typeEnum){
        QueryWrapper query = new QueryWrapper();
        query.eq("object_id", article.getArticleId());
        query.eq("user_id", userId);
        query.eq("op_type", typeEnum.getType());
        LikeRecord record = likeRecordMapper.selectOne(query);
        if(record != null){//点赞过了 删除并更新点赞
            likeRecordMapper.delete(query);
            forumArticleMapper.updateArticleCountMessage(UpdateArticleTypeEnum.GOOD_COUNT.getType(), -1, article.getArticleId());
        }else{//没有点赞记录 查询是否存在
            LikeRecord record1 = new LikeRecord();
            record1.setObjectId(article.getArticleId());
            record1.setOpType(typeEnum.getType());
            record1.setCreateTime(new Date());
            record1.setAuthorUserId(article.getUserId());
            record1.setUserId(userId);
            likeRecordMapper.insert(record1);
            forumArticleMapper.updateArticleCountMessage(UpdateArticleTypeEnum.GOOD_COUNT.getType(), 1, article.getArticleId());
        }
        return record;
    }
}
