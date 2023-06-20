package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.*;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageStatusEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageTypeEunm;
import club.chillrainqcna.chillrainbbs.entity.enums.UserIntegralOperTypeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.*;
import club.chillrainqcna.chillrainbbs.service.AttachmentService;
import club.chillrainqcna.chillrainbbs.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;

/**
 * @author ChillRain 2023 04 23
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Resource
    private WebConfig webConfig;
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ForumArticleAttachmentMapper forumArticleAttachmentMapper;
    @Resource
    private ForumArticleAttachmentDownloadMapper forumArticleAttachmentDownloadMapper;
    @Resource
    private UserMessageMapper userMessageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ForumArticleAttachment downloadAttachment(String fileId, SessionWebUser sessionWebUser) {
        UserInfo user = userMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_id", sessionWebUser.getUserId()));
        Integer currentIntegral = user.getCurrentIntegral();
        ForumArticleAttachment attachment = forumArticleAttachmentMapper.selectOne(new QueryWrapper<ForumArticleAttachment>().eq("file_id", fileId));
        if(attachment == null){
            throw new ChillRainBBSException("文件不存在");
        }
        Integer attachmentIntegral = attachment.getIntegral();
        ForumArticleAttachmentDownload record = forumArticleAttachmentDownloadMapper.selectOne(new QueryWrapper<ForumArticleAttachmentDownload>().eq("file_id", fileId).eq("user_id", sessionWebUser.getUserId()));
        if(attachmentIntegral > 0 && !attachment.getUserId().equals(user.getUserId())){//不是作者 && 下载积分 > 0
            if(record == null){
                if(currentIntegral - attachmentIntegral < 0){
                    throw new ChillRainBBSException("积分不足");
                }
            }
        }
        ForumArticleAttachmentDownload updateDownload = new ForumArticleAttachmentDownload();
        //记录下载信息 并更新下载次数
        updateDownload.setDownloadCount(1);
        updateDownload.setUserId(user.getUserId());
        updateDownload.setArticleId(attachment.getArticleId());
        updateDownload.setFileId(fileId);
        forumArticleAttachmentDownloadMapper.insertOrUpdate(updateDownload);
        forumArticleAttachmentMapper.updateDownloadCount(fileId);
        //扣除积分 是作者不扣积分 不是则要扣除
        if(user.getUserId().equals(attachment.getUserId())){
            return attachment;
        }
        if(record != null){
            return attachment;
        }
        //操作用户积分
        userService.userIntegralOper(user.getUserId(), UserIntegralOperTypeEnum.USER_DOWNLOAD_ATTACHMENT, -1, attachmentIntegral);
        userService.userIntegralOper(attachment.getUserId(), UserIntegralOperTypeEnum.DOWNLOAD_ATTACHMENT, 1, attachmentIntegral);
        //发送信息
        ForumArticle article = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", attachment.getArticleId()));
        UserMessage message = new UserMessage();
        message.setMessageType(MessageTypeEunm.DOWNLOAD_ATTACHMENT.getType());
        message.setSendUserId(user.getUserId());
        message.setReceivedUserId(attachment.getUserId());
        message.setCreateTime(new Date());
        message.setArticleTitle(article.getTitle());
        message.setCommentId(0);
        message.setSendNickName(user.getNickName());
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        message.setArticleId(article.getArticleId());
        userMessageMapper.insert(message);

        return attachment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, String fileId) {
        InputStream in = null;
        OutputStream out = null;
//        ForumArticleAttachment attachment = forumArticleAttachmentMapper.selectOne(new QueryWrapper<ForumArticleAttachment>().eq("file_id", fileId));
        ForumArticleAttachment attachment = downloadAttachment(fileId, (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY));
        String downloadFileName = attachment.getFileName();
        String filePath = webConfig.getProjectFolder() + downloadFileName;
        File file = new File(filePath);
        try{
            //创建流
            in = new FileInputStream(filePath);
            out = response.getOutputStream();
            //设置中文 不知道为什么报错
            response.setContentType("application/x-msdownload; charset=utf-8");
//            downloadFileName = new String(downloadFileName.getBytes("utf-8"), "ISO8859-1");
//            response.setHeader("Content-Disposition", "attachment;filename=\"" + downloadFileName + "\"");

            byte[] bytes = new byte[1024];
            int len = 0;
            while((len = in.read(bytes)) != -1){
                out.write(bytes, 0, len);
            }
            out.flush();
        }catch (Exception e){
            throw new ChillRainBBSException("下载失败");
        }finally {
            try {
                if(in != null) in.close();
                if(out != null) out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
