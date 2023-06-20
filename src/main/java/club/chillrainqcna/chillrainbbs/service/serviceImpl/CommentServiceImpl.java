package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.*;
import club.chillrainqcna.chillrainbbs.entity.dto.FileUploadDto;
import club.chillrainqcna.chillrainbbs.entity.enums.*;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.ForumArticleMapper;
import club.chillrainqcna.chillrainbbs.mappers.ForumCommentMapper;
import club.chillrainqcna.chillrainbbs.mappers.UserMapper;
import club.chillrainqcna.chillrainbbs.mappers.UserMessageMapper;
import club.chillrainqcna.chillrainbbs.service.CommentService;
import club.chillrainqcna.chillrainbbs.utils.FileUtil;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import club.chillrainqcna.chillrainbbs.utils.StringUtil;
import club.chillrainqcna.chillrainbbs.utils.SystemSettingUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.logging.log4j.message.Message;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author ChillRain 2023 04 24
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private FileUtil fileUtil;
    @Resource
    private ForumCommentMapper forumCommentMapper;

    @Resource
    private ForumArticleMapper forumArticleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserMessageMapper userMessageMapper;
    @Lazy
    @Resource
    private CommentService commentService;
    /**
     * 评论载入
     * @param session
     * @param articleId
     * @param commentPageNumber
     * @param orderType
     * @return
     */
    @Override
    public List<ForumComment> loadComment(HttpSession session, String articleId, Integer commentPageNumber, Integer orderType) {
        if(!SystemSettingUtil.getSystemSetting().getComment().getCommentOpen()){//评论功能关闭
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        String orderBy = null;
        if(orderType == 0){
            orderBy = "good_count";//热榜
        }
        if(orderType == 1){
            orderBy = "comment_id";//发布时间
        }
        QueryWrapper<ForumComment> query = new QueryWrapper<>();
        query.eq("article_id", articleId).eq("p_comment_id", 0);//查询一级评论
//        Page<ForumComment> page = new Page<>(commentPageNumber, 10);//设置分页
        query.orderByDesc(orderBy).orderByDesc("top_type");//设置排序 + 置顶
        //登录后查询是否点赞
//        Page<ForumComment> commentPage = forumCommentMapper.selectPage(page, query);
//        return commentPage.getRecords();
        return comment2Tree(forumCommentMapper.selectList(query));
    }

    @Override
    public void topComment(String userId, Integer commentId, Integer topType) {
        TopTypeEnum topTypeEnumByType = TopTypeEnum.getTopTypeEnumByType(topType);
        if(topTypeEnumByType == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        //查询被评论的文章是否是 当前的作者 即只有文章作者才能置顶
        ForumComment comment = forumCommentMapper.selectOne(new QueryWrapper<ForumComment>().eq("comment_id", commentId));
        //参数校验
        if(comment == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        ForumArticle article = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("comment_id", comment.getArticleId()));
        if(article == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        if(!article.getUserId().equalsIgnoreCase(userId) || comment.getPCommentId() != 0){
            throw new ChillRainBBSException("权限不足");
        }
        if(comment.getTopType().equals(topType)){
            return;
        }
        //如果已置顶 则取消置顶
//        if()
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void postComment(HttpSession session, String articleId, Integer pCommentId, String content, MultipartFile image, String replayUserId) {
        if(!SystemSettingUtil.getSystemSetting().getComment().getCommentOpen()){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        if(image == null && NotNullUtil.isEmpty(content)){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        if(user == null){
            throw new ChillRainBBSException("未登录");
        }
        content = StringUtil.content2Html(content);
        ForumComment comment = new ForumComment();
        comment.setContent(content);
        comment.setReplyUserId(replayUserId);
        comment.setUserId(user.getUserId());
        comment.setNickName(user.getNikeName());
        comment.setArticleId(articleId);
        comment.setPCommentId(pCommentId);
        comment.setUserIpAddress(user.getProvince());
        comment.setTopType(TopTypeEnum.NO_TOP.getTopType());
        forumCommentMapper.insert(comment);
//        if(pCommentId != 0){//是子评论 查出来全传过去
//            List<ForumComment> forumComments = forumCommentMapper.selectList(new QueryWrapper<ForumComment>().eq("article_id", articleId).eq("p_comment_id", pCommentId).orderByAsc("comment_id"));
////            return forumComments;
//        }
//        return comment;
        ForumArticle article = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", articleId));
        //应当给被评论者一个消息通知
        UserMessage message = new UserMessage();
//        message.setCommentId();
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        message.setArticleId(articleId);
        message.setMessageType(MessageTypeEunm.COMMENT.getType());
        message.setReceivedUserId(article.getUserId());
        message.setSendUserId(user.getUserId());
        message.setCreateTime(new Date());
        message.setSendNickName(user.getNikeName());
        message.setMessageContent(user.getNikeName() + "评论了你的文章：" + content);
        userMessageMapper.insert(message);

    }

    @Override
    public void delComments(String commentIds) {
        String[] ids = commentIds.split(",");
        for (String id : ids) {
            commentService.delComment(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delComment(String commentId) {
        ForumComment comment = forumCommentMapper.selectOne(new QueryWrapper<ForumComment>().eq("comment_id", Integer.parseInt(commentId)));
        if(comment == null || comment.getStatus().equals(CommentStatusEnum.DEL.getStatus())){
            return;
        }
        ForumComment forumComment = new ForumComment();
        forumComment.setStatus(CommentStatusEnum.DEL.getStatus());
        forumCommentMapper.update(forumComment, new QueryWrapper<ForumComment>().eq("comment_id", Integer.parseInt(commentId)));

        //修改文章相关信息
        if(comment.getStatus().equals(CommentStatusEnum.AUDIT.getStatus())){
            if(comment.getPCommentId() == 0){//更新一级评论数量
                forumArticleMapper.updateArticleCountMessage(UpdateArticleTypeEnum.COMMENT_COUNT.getType(), -1, comment.getArticleId());
            }
            //修改相关用户的积分信息
            Integer integer = SystemSettingUtil.getSystemSetting().getComment().getCommentIntegeral();
            userMapper.updateIntegral(comment.getUserId(), -integer);
        }
        //发送消息
        UserMessage message = new UserMessage();
        message.setReceivedUserId(comment.getUserId());
        message.setCreateTime(new Date());
        message.setMessageType(MessageTypeEunm.SYS.getType());
        message.setMessageContent("评论【" + comment.getContent() + "】被管理员删除");
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        userMessageMapper.insert(message);
    }

    @Override
    public void aduitComments(String commentIds) {
        String[] ids = commentIds.split(",");
        for (String id : ids) {
            commentService.aduitComments(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aduitComment(String commentId) {
        Integer id = Integer.parseInt(commentId);
        ForumComment comment = forumCommentMapper.selectOne(new QueryWrapper<ForumComment>().eq("comment_id", id));
        if(comment == null || comment.getStatus().equals(CommentStatusEnum.AUDIT.getStatus())){
            return;
        }
        ForumComment forumComment = new ForumComment();
        forumComment.setStatus(CommentStatusEnum.AUDIT.getStatus());
        forumCommentMapper.update(forumComment, new QueryWrapper<ForumComment>().eq("comment_id", id));
        ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", comment.getArticleId()));
        ForumComment pComment = null;
        if(comment.getPCommentId() != 0 && NotNullUtil.isEmpty(comment.getReplyUserId())){
            pComment = forumCommentMapper.selectOne(new QueryWrapper<ForumComment>().eq("p_comment_id", comment.getPCommentId()));
        }
        updateCommentInfo(comment, forumArticle, pComment);
//
    }

    //    /**
//     * 真正发送评论的方法
//     * @param comment
//     * @param file
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void postCommentDo(ForumComment comment, MultipartFile file){
//        //被评论的文章应该存在 且应该已经被审核
//        ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", comment.getArticleId()));
//        if(forumArticle == null || forumArticle.getStatus().equals(ArticleStatusEnum.NOT_AUDIT)){
//            throw new ChillRainBBSException("评论的文章不存在");
//        }
//        ForumComment pComment = null;
//        if(comment.getPCommentId() != 0){//是子评论 先查出来他的父评论
//            pComment = forumCommentMapper.selectOne(new QueryWrapper<ForumComment>().eq("comment_id", comment.getPCommentId()));
//            if(pComment == null){
//                throw new ChillRainBBSException("评论不存在");
//            }
//        }
//        //判断回复的用户是否存在
//        if(!NotNullUtil.isEmpty(comment.getReplyUserId())){
//            UserInfo user = userMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_id", comment.getReplyUserId()));
//            if(user == null){
//                throw new ChillRainBBSException("回复的用户不存在");
//            }
//            comment.setReplyUserId(user.getUserId());
//        }
//        comment.setPostTime(new Date());
//        if(file != null){ //评论带图片
//            FileUploadDto filedto = fileUtil.uploadFile2Loacl(file, Constant.FILE_FOLDER_IMAGE, FileUploadTypeEnum.COMMENT_IMAGE);
//            comment.setImgPath(filedto.getLocalPath());
//        }
//        Boolean needAudit = SystemSettingUtil.getSystemSetting().getAudit().getCommentAudit();
//        comment.setStatus(needAudit ? CommentStatusEnum.AUDIT.getStatus() : CommentStatusEnum.NOT_AUDIT.getStatus());
//        forumCommentMapper.insert(comment);
//        if(needAudit){
//            //审核通过 更新用户积分，文章的评论信息
//            return;
//        }
//        updateCommentInfo(comment, forumArticle, pComment);
//    }
    public void updateCommentInfo(ForumComment comment, ForumArticle article, ForumComment pComment){
        Integer integral = SystemSettingUtil.getSystemSetting().getComment().getCommentIntegeral();
        if(integral > 0){//更新用户积分
            userMapper.updateIntegral(comment.getUserId(), integral);
        }
        if(comment.getPCommentId() == 0){//评论的是文章 更新文章信息
            forumArticleMapper.updateArticleCountMessage(UpdateArticleTypeEnum.COMMENT_COUNT.getType(), 1, article.getArticleId());
        }
        //发送消息
        UserMessage message = new UserMessage();
        message.setMessageType(MessageTypeEunm.COMMENT.getType());
        message.setCreateTime(new Date());
        message.setArticleId(article.getArticleId());
        message.setCommentId(comment.getCommentId());
        message.setSendUserId(comment.getUserId());
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        message.setArticleTitle(article.getTitle());
        if(comment.getCommentId() == 0){//回复的是文章作者
            message.setReceivedUserId(article.getUserId());
        } else if (comment.getCommentId() != 0 && NotNullUtil.isEmpty(comment.getReplyUserId())) {//回复的是评论
            message.setReceivedUserId(pComment.getUserId());
        }else if (comment.getCommentId() != 0 && !NotNullUtil.isEmpty(comment.getReplyUserId())) {//回复的是评论的评论
            message.setReceivedUserId(comment.getReplyUserId());
        }
        if(!comment.getUserId().equals(message.getReceivedUserId())){//信息的发起人与接收人不同
            userMessageMapper.insert(message);
        }
    }

    /** BTS
     * 转化为评论树
     * @return
     */
    private List<ForumComment> comment2Tree(List<ForumComment> data){
        Iterator<ForumComment> iterator = data.iterator();
        while (iterator.hasNext()){
            ForumComment next = iterator.next();
            Integer commentId = next.getCommentId();
            List<ForumComment> children = forumCommentMapper.selectList(new QueryWrapper<ForumComment>().eq("p_comment_id", commentId));
            next.setChildren(children);
        }
//        ArrayList<ForumComment> children = new ArrayList<>();
//
//        for (ForumComment comment : data) {
//            if(comment.getPCommentId() == (pCommentId)){
//                comment.setChildren(comment2Tree(data,comment.getCommentId()));
//                children.add(comment);
//            }
//        }

//        Page<ForumComment> commentPage = forumCommentMapper.selectPage(page, query);
//        System.out.println(children);
//        return children;
        return null;
    }
}
