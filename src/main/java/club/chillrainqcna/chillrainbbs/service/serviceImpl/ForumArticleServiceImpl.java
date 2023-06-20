package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.*;
import club.chillrainqcna.chillrainbbs.entity.dto.FileUploadDto;
import club.chillrainqcna.chillrainbbs.entity.enums.*;
import club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4AuditDto;
import club.chillrainqcna.chillrainbbs.entity.vo.UserDownloadVO;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.*;
import club.chillrainqcna.chillrainbbs.service.AttachmentService;
import club.chillrainqcna.chillrainbbs.service.ForumArticleService;
import club.chillrainqcna.chillrainbbs.service.ForumService;
import club.chillrainqcna.chillrainbbs.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.rmi.server.ExportException;
import java.util.Date;
import java.util.List;

/**
 * @author ChillRain 2023 04 20
 */
@Service
public class ForumArticleServiceImpl implements ForumArticleService {
    @Resource
    private FileUtil fileUtil;
    @Resource
    private ForumService forumService;
    @Resource
    private ImageUtil imageUtil;
    @Resource
    private WebConfig webConfig;
    @Resource
    private ForumArticleAttachmentMapper forumArticleAttachmentMapper;
    @Resource
    private ForumArticleAttachmentDownloadMapper forumArticleAttachmentDownloadMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private LikeRecordMapper likeRecordMapper;
    @Resource
    private UserMessageMapper userMessageMapper;
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private ForumMapper forumMapper;
    @Lazy
    @Resource
    private ForumArticleService forumArticleService;



    @Override
    public ForumArticle readArticle(HttpSession session, String articleId) {
        if(NotNullUtil.isEmpty(articleId)){
            throw new ChillRainBBSException("错误的文章ID");
        }
        QueryWrapper<ForumArticle> query = new QueryWrapper<>();
        query.eq("article_id", articleId);
        ForumArticle forumArticle = forumArticleMapper.selectOne(query);
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
//        || !user.getIsAdmin().equals("false")
        if(forumArticle == null ){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_404);
        }
        forumArticleMapper.updateArticleCountMessage(UpdateArticleTypeEnum.READ_COUNT.getType(), 1, articleId);
        return forumArticle;
    }

    @Override
    public List<ForumArticle> loadArticle(Integer boardId, Integer pBoardId, Integer orderType, Integer pageNo) {
        QueryWrapper<ForumArticle> query = new QueryWrapper<>();
        query.eq("board_id",(boardId == null  ? 0 : boardId));
        query.eq("p_board_id",pBoardId);
        query.eq("status", 1);
        query.select("article_id","board_id","board_name","p_board_id",
                "p_board_name","user_id","nick_name","user_ip_address","title",
                "cover","editor_type","summary","post_time","last_update_time",
                "read_count","good_count","comment_count","top_type","attachment_type","status");
        query.orderByDesc("read_count");
        Page<ForumArticle> page = new Page<>(pageNo,2);
        Page<ForumArticle> forumArticleIPage = forumArticleMapper.selectPage(page, query);

        return forumArticleIPage.getRecords();
    }

    @Override
    public UserDownloadVO getUserDownloadInfo(HttpSession session, String fileId) {
        SessionWebUser webUser = (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY);
        UserInfo user = userMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_id", webUser.getUserId()));
        UserDownloadVO userDownloadVO = new UserDownloadVO();
        userDownloadVO.setUserIntegral(user.getCurrentIntegral());
        ForumArticleAttachmentDownload record = forumArticleAttachmentDownloadMapper.selectOne(new QueryWrapper<ForumArticleAttachmentDownload>().eq("file_id", fileId).eq("user_id", user.getUserId()));
        if(record != null){//有过记录 不用再扣除积分
            userDownloadVO.setHaveDownload(true);
        }
        return userDownloadVO;
    }

    @Override
    public List<ForumBoard> loadBoard4Post(HttpSession session) {
        SessionWebUser user = (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY);
        Integer postType = null;
        if(user.getIsAdmin().equals("false")){//不是管理员 只能查询类型为1的(任何人可以发帖)
            postType = 1;
        }
        List<ForumBoard> forumBoard = forumService.getForumBoard(postType);
        return forumBoard;
    }

    @Override
    public ForumArticle postArticle(HttpSession session, MultipartFile cover, MultipartFile attachment, Integer integral, String title, Integer pBoard, Integer boardId, String summary, Integer editorType, String content, String markdownContent) {
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        title = StringUtil.content2Html(title);//去除HTML标签
        ForumArticle article = new ForumArticle();
        article.setTitle(title);
        article.setArticleId(user.getUserId());
        article.setNickName(user.getNikeName());
        article.setContent(content);
        article.setPBoardId(pBoard);
        article.setSummary(summary);
        article.setBoardId(boardId);
        article.setUserIpAddress(user.getProvince());
        if(editorType == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        if(EditorTypeEnum.MARKDOWN_EDITOR.getType().equals(editorType) && NotNullUtil.isEmpty(markdownContent)){//是markdown编辑器且没有内容
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        article.setMarkdownContent(markdownContent);
        article.setEditorType(editorType);
        //设置附件信息
        ForumArticleAttachment articleAttachment = new ForumArticleAttachment();
        articleAttachment.setIntegral(integral == null ? 0 : integral);

        postArticleDo(attachment, article, articleAttachment, cover, user.getIsAdmin().equals("true"));
        return article;
    }

    @Override
    public void aduitArticles(String articleIds) {
        String[] ids = articleIds.split(",");
        for (String id : ids) {
            forumArticleService.aduitArticle(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aduitArticle(String articleId) {
        ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", articleId));
        if(articleId == null || forumArticle.getStatus().equals(ArticleStatusEnum.AUDIT.getStatus())){
            return;
        }
        forumArticle.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        forumArticleMapper.updateById(forumArticle);
        //审核完成 加积分
        Integer integeral = SystemSettingUtil.getSystemSetting().getPost().getPostIntegeral();
        if(integeral > 0 && forumArticle.getStatus().equals(ArticleStatusEnum.NOT_AUDIT.getStatus())){
            userMapper.updateIntegral(forumArticle.getUserId(), integeral);
        }
        //发消息
        UserMessage message = new UserMessage();
        message.setReceivedUserId(forumArticle.getUserId());
        message.setMessageType(MessageTypeEunm.SYS.getType());
        message.setCreateTime(new Date());
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        message.setMessageContent("文章【" + forumArticle.getTitle() + "】已通过");
        userMessageMapper.insert(message);

    }

    @Override
    public void updateBoardInfo(String articleId, Integer pBoardId, Integer boardId) {
        ForumArticle article = new ForumArticle();
        article.setPBoardId(pBoardId);
        article.setBoardId(boardId);
        resetBoardInfo(true, article);
        forumArticleMapper.update(article, new QueryWrapper<ForumArticle>().eq("article_id", articleId));
    }

    @Override
    public void delArticleByIds(List<String> list) {
        for (String id : list) {
            forumArticleService.delArticleById(id);
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public void delArticleById(String is){
        ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", is));
        if(forumArticle == null ||forumArticle.getStatus().equals(ArticleStatusEnum.DEL.getStatus())){
            return;
        }
        forumArticle.setStatus(ArticleStatusEnum.DEL.getStatus());
        forumArticleMapper.update(forumArticle,new QueryWrapper<ForumArticle>().eq("article_id", is));

        //删完了 删除其积分
        Integer integeral = SystemSettingUtil.getSystemSetting().getPost().getPostIntegeral();
        if(integeral > 0 && ArticleStatusEnum.AUDIT.getStatus().equals(forumArticle.getStatus())){
            userMapper.updateIntegral(forumArticle.getUserId(), -integeral);
        }
        //消息通知
        UserMessage message = new UserMessage();
        message.setReceivedUserId(forumArticle.getUserId());
        message.setMessageType(MessageTypeEunm.SYS.getType());
        message.setCreateTime(new Date());
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        message.setMessageContent("文章【" + forumArticle.getTitle() + "】已被管理员删除");
        userMessageMapper.insert(message);
    }

    @Override
    public ForumArticle postArticle4Update(HttpSession session, MultipartFile cover, MultipartFile attachment, Integer integral, String articleId, String title, Integer pBoard, Integer boardId, String summary, Integer editorType, String content, String markdownContent, Integer attachmentType) {
        title = StringUtil.content2Html(title);
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        ForumArticle article = new ForumArticle();
        article.setArticleId(articleId);
        article.setPBoardId(pBoard);
        article.setBoardId(boardId);
        article.setTitle(title);
        article.setContent(content);
        article.setMarkdownContent(markdownContent);
        article.setEditorType(editorType);
        article.setSummary(summary);
        article.setUserIpAddress(user.getProvince());
        article.setUserId(user.getUserId());

        ForumArticleAttachment articleAttachment = new ForumArticleAttachment();
        articleAttachment.setIntegral(integral == null ? 0 : integral);

        updateArticle(user.getIsAdmin().equals("true"), article, articleAttachment, cover, attachment);
        return null;
    }
    private void updateArticle(Boolean isAdmin, ForumArticle article, ForumArticleAttachment attachment, MultipartFile cover, MultipartFile attachmentFile){
        ForumArticle dbInfo = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", article.getArticleId()));
        if(!isAdmin && !dbInfo.getUserId().equals(article.getUserId())){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        //设定信息
        article.setLastUpdateTime(new Date());
        resetBoardInfo(isAdmin, article);
        if(cover != null){//封面设定
            FileUploadDto fileUploadDto = fileUtil.uploadFile2Loacl(cover, Constant.FILE_FOLDER_IMAGE, FileUploadTypeEnum.ARTICLE_COVER);
            article.setCover(fileUploadDto.getLocalPath());
        }
        if(attachmentFile != null){//附件设定
//            FileUploadDto fileUploadDto = fileUtil.uploadFile2Loacl(attachmentFile, Constant.FILE_FOLDER_ATTACHMENT, FileUploadTypeEnum.ARTICLE_ATTACHMENT);
            updateAttachment(attachment, article, attachmentFile, true);
            article.setAttachmentType(ArticleAttachmentTypeEnum.NO.getType());
        }

        //查询附件 如果传来的附件为空 那就删除 然后清空数据库记录
        ForumArticleAttachment dbAttachment = null;
        List<ForumArticleAttachment> attachments = forumArticleAttachmentMapper.selectList(new QueryWrapper<ForumArticleAttachment>().eq("article_id", article.getArticleId()));
        if(attachments != null){
            dbAttachment = attachments.get(0);
        }
        if(dbAttachment != null){//数据库记录不为空
            if(article.getAttachmentType() == 0){//但是传送来的article的附件类型为空 删除相关信息
                new File(webConfig.getProjectFolder() + Constant.FILE_FOLDER_ATTACHMENT + dbAttachment.getFilePath()).delete();
                forumArticleAttachmentMapper.delete(new QueryWrapper<ForumArticleAttachment>().eq("file_id", dbAttachment.getFileId()));
            }else{
                //更新积分
                if(!dbAttachment.getIntegral().equals(attachment.getIntegral())){
                    ForumArticleAttachment attachmentUpdate = new ForumArticleAttachment();
                    attachmentUpdate.setIntegral(attachment.getIntegral());
                    forumArticleAttachmentMapper.update(attachmentUpdate, new QueryWrapper<ForumArticleAttachment>().eq("file_id", dbAttachment.getFileId()));
                }
            }
        }
        //设定审核
        if(isAdmin){
            article.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }else{
            SystemSetting4AuditDto audit = SystemSettingUtil.getSystemSetting().getAudit();
            article.setStatus(audit.getPostAudit() ? ArticleStatusEnum.NOT_AUDIT.getStatus() : ArticleStatusEnum.AUDIT.getStatus());
        }
        //图片修改
        String content = article.getContent();
        if(!NotNullUtil.isEmpty(content)){
            String month = imageUtil.resetImageHtml(content);
            String replaceMonth = "/" + month + "/";
            content = content.replace(Constant.FILE_FOLDER_TEMP, replaceMonth);
            article.setContent(content);
            String markdownContent = article.getMarkdownContent();
            if(!NotNullUtil.isEmpty(markdownContent)){
                markdownContent = markdownContent.replace(Constant.FILE_FOLDER_TEMP, month);
                article.setMarkdownContent(markdownContent);
            }
        }
        forumArticleMapper.update(article, new QueryWrapper<ForumArticle>().eq("article_id", article.getArticleId()));
    }

    private void postArticleDo(MultipartFile attachmentFile, ForumArticle article, ForumArticleAttachment attachment, MultipartFile cover, Boolean isAdmin){
        resetBoardInfo(isAdmin, article);
        Date curDate = new Date();
        String articleId = StringUtil.getRandomNumber(15);
        article.setArticleId(articleId);
        article.setPostTime(curDate);
        article.setLastUpdateTime(curDate);
        if(cover != null){//有封面
            FileUploadDto fileUploadDto = fileUtil.uploadFile2Loacl(cover, Constant.FILE_FOLDER_IMAGE, FileUploadTypeEnum.ARTICLE_COVER);
            article.setCover(fileUploadDto.getLocalPath());
        }
        if(attachment != null){//带有附件
            updateAttachment(attachment, article, attachmentFile, false);
            article.setAttachmentType(ArticleAttachmentTypeEnum.YES.getType());
        }else{
            article.setAttachmentType(ArticleAttachmentTypeEnum.NO.getType());
        }
        //审核设定
        if(isAdmin){
            article.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }else{
            SystemSetting4AuditDto audit = SystemSettingUtil.getSystemSetting().getAudit();
            article.setStatus(audit.getPostAudit() ? ArticleStatusEnum.NOT_AUDIT.getStatus() : ArticleStatusEnum.AUDIT.getStatus());
        }
        //图片 把临时图片转存
        String content = article.getContent();
        if(!NotNullUtil.isEmpty(content)){
            String month = imageUtil.resetImageHtml(content);
            String replaceMonth = "/" + month + "/";
            content = content.replace(Constant.FILE_FOLDER_TEMP, replaceMonth);
            article.setContent(content);
            String markdownContent = article.getMarkdownContent();
            if(!NotNullUtil.isEmpty(markdownContent)){
                markdownContent = markdownContent.replace(Constant.FILE_FOLDER_TEMP, month);
                article.setMarkdownContent(markdownContent);
            }
        }

        forumArticleMapper.insert(article);
        //增加用户积分
        Integer integeral = SystemSettingUtil.getSystemSetting().getPost().getPostIntegeral();
        if(integeral > 0 && article.getStatus().equals(ArticleStatusEnum.AUDIT.getStatus())){
            userMapper.updateIntegral(article.getUserId(),integeral);
        }
    }
    private void updateAttachment(ForumArticleAttachment attachment, ForumArticle article, MultipartFile file, Boolean isUpdate){
        Integer attachmentMaxSize = SystemSettingUtil.getSystemSetting().getPost().getAttachmentSize();
        long allowSize = attachmentMaxSize * Constant.FILE_SIZE_1M;
        if(file.getSize() > allowSize){
            throw new ChillRainBBSException("附件大小超过允许大小(" + attachmentMaxSize + ")MB");
        }
        ForumArticleAttachment dbInfo = null;//附件信息
        if(isUpdate){//更新
            //先查询之前的上传记录（可能是一个列表）
            List<ForumArticleAttachment> attachments = forumArticleAttachmentMapper.selectList(new QueryWrapper<ForumArticleAttachment>().eq("article_id", article.getArticleId()));
            if(attachments != null){
                dbInfo = attachments.get(0);
                new File(webConfig.getProjectFolder() + Constant.FILE_FOLDER_ATTACHMENT + dbInfo.getFilePath()).delete();
            }
        }
        //上传附件
        FileUploadDto fileUploadDto = fileUtil.uploadFile2Loacl(file, Constant.FILE_FOLDER_ATTACHMENT, FileUploadTypeEnum.ARTICLE_ATTACHMENT);

        if(dbInfo == null){//服务器没有信息 插入
            attachment.setFileId(StringUtil.getRandomNumber(15));
            attachment.setFileName(fileUploadDto.getOriginalFileName());
            attachment.setFileSize(file.getSize());
            attachment.setFilePath(fileUploadDto.getLocalPath());
            attachment.setDownloadCount(0);
            attachment.setUserId(article.getUserId());
            attachment.setFileType(AttachmentFileTypeEnum.ZIP.getType());
            //插入附件信息于数据库
            forumArticleAttachmentMapper.insert(attachment);
        }else{//有信息 更新数据库
            ForumArticleAttachment attachment1 = new ForumArticleAttachment();
            attachment1.setFileName(fileUploadDto.getOriginalFileName());
            attachment1.setFileSize(file.getSize());
            attachment1.setFilePath(fileUploadDto.getLocalPath());
            forumArticleAttachmentMapper.update(attachment1, new QueryWrapper<ForumArticleAttachment>().eq("file_id", dbInfo.getFileId()));

        }

    }
    private void resetBoardInfo(Boolean isAdmin, ForumArticle article){
        Integer pBoardId = article.getPBoardId();
        ForumBoard pBoard = forumMapper.selectOne(new QueryWrapper<ForumBoard>().eq("p_board_id", pBoardId));
        if(pBoard == null ||pBoard.getPostType() == 0 && !isAdmin){//一级板块不存在 一级板块不可发帖
            throw new ChillRainBBSException("一级板块不存在");
        }
        article.setPBoardName(pBoard.getBoardName());
        if(article.getBoardId() != null && article.getBoardId() != 0){
            ForumBoard board = forumMapper.selectOne(new QueryWrapper<ForumBoard>().eq("board_id", article.getBoardId()));
            if(board == null || board.getPostType() == 0 && !isAdmin){
                throw new ChillRainBBSException("二级板块不存在");
            }
            article.setBoardName(board.getBoardName());

        }else{
            article.setBoardId(0);
            article.setBoardName("");
        }

    }

//    @Override
//    public void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, String fileId) {
//        InputStream  in = null;
//        OutputStream out = null;
////        ForumArticleAttachment attachment = forumArticleAttachmentMapper.selectOne(new QueryWrapper<ForumArticleAttachment>().eq("file_id", fileId));
//        ForumArticleAttachment attachment = attachmentService.downloadAttachment(fileId, (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY));
//        String downloadFileName = attachment.getFileName();
//        String filePath = webConfig.getProjectFolder() + downloadFileName;
//        File file = new File(filePath);
//        try{
//            //创建流
//            in = new FileInputStream(filePath);
//            out = response.getOutputStream();
//            //设置中文
//            response.setContentType("application/x-msdownload; charset=utf-8");
//            downloadFileName = new String(downloadFileName.getBytes("utf-8"), "ISO8859-1");
//            response.setHeader("Content-Disposition", "attachment;filename=\"" + downloadFileName + "\"");
//
//            byte[] bytes = new byte[1024];
//            int len = 0;
//            while((len = in.read(bytes)) != -1){
//                out.write(bytes, 0, len);
//            }
//            out.flush();
//        }catch (Exception e){
//            throw new ChillRainBBSException("下载失败");
//        }finally {
//            try {
//                in.close();
//                out.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//    public Integer

    @Override
    public void doLike(String objectId, String userId, String nickName, DoLikeTypeEnum typeEnum) {
        UserMessage message = new UserMessage();
        message.setCreateTime(new Date());
        LikeRecord record = null;
        switch (typeEnum){
            case ARTICLE_LIKE:
                ForumArticle article = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", objectId));
                if(article == null){
                    throw new ChillRainBBSException("文章不存在");
                }
                record = articleLike(article,userId,nickName,typeEnum);
                message.setArticleId(objectId);
                message.setArticleTitle(null);
                message.setMessageType(MessageTypeEunm.ARTICLE_LIKE.getType());
                message.setArticleTitle(article.getTitle());
                message.setReceivedUserId(article.getUserId());
                break;
            case COMMENT_LIKE:
                break;
        }
        message.setSendUserId(userId);
        message.setSendNickName(nickName);
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        if(record == null && !userId.equals(message.getReceivedUserId())){//没点赞  自赞
            userMessageMapper.insert(message);
        }
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
//            return null;
        }else{//没有点赞记录 查询是否存在
//            query = new QueryWrapper();
//            query.eq("article_id", article.getArticleId());
//            ForumArticle forumArticle = forumArticleMapper.selectOne(query);
//            if(forumArticle == null){
//                throw new ChillRainBBSException("文章不存在");
//            }
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
