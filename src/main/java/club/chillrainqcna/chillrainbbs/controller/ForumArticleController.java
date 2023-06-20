package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.*;
import club.chillrainqcna.chillrainbbs.entity.enums.DoLikeTypeEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.ForumArticleMapper;
import club.chillrainqcna.chillrainbbs.mappers.UserMapper;
import club.chillrainqcna.chillrainbbs.service.AttachmentService;
import club.chillrainqcna.chillrainbbs.service.ForumArticleService;
import club.chillrainqcna.chillrainbbs.service.LikeRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ChillRain 2023 04 20
 */
@RestController
@RequestMapping("/forum")
public class ForumArticleController extends BASEController {
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private LikeRecordService likeRecordService;
    @Resource
    private ForumArticleService forumArticleService;

    /**
     * 已测试
     * @param session
     * @param request
     * @param boardId
     * @param pBoardId
     * @param orderType
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadArticle")
    public Response loadArticle(HttpSession session,
                                HttpServletRequest request,
                                Integer boardId,
                                Integer pBoardId,
                                Integer orderType,
                                Integer pageNo){
//        QueryWrapper<ForumArticle> query = new QueryWrapper<>();
//        query.eq("board_id",(boardId == null  ? 0 : boardId));
//        query.eq("p_board_id",pBoardId);
//        query.eq("status", 1);
//        query.select("article_id","board_id","board_name","p_board_id",
//                        "p_board_name","user_id","nick_name","user_ip_address","title",
//                        "cover","editor_type","summary","post_time","last_update_time",
//                        "read_count","good_count","comment_count","top_type","attachment_type","status");
//        query.orderByDesc("read_count");
//        Page<ForumArticle> page = new Page<>(pageNo,10);
//        Page<ForumArticle> forumArticleIPage = forumArticleMapper.selectPage(page, query);
        return getSuccessResponse(forumArticleService.loadArticle(boardId, pBoardId, orderType, pageNo));

    }

    /**
     * 已测试
     * @param session
     * @param articleId
     * @return
     */
    @RequestMapping("/getArticleDetail/{ArticleId}")
    public Response getArticleDetail(HttpSession session, @PathVariable(name = "ArticleId") String articleId){

//        String content = forumArticle.getContent();
        return getSuccessResponse(forumArticleService.readArticle(session, articleId));
    }

    /**
     * 已测试
     * @param session
     * @param articleId
     * @return
     */
    @RequestMapping("/dolike")
    @GobalAnnotation(needLogin = true)
    public Response doLike(HttpSession session, String articleId){
        SessionWebUser user = (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY);
//        forumArticleService.doLike(articleId, user.getUserId(),user.getNikeName(), DoLikeTypeEnum.ARTICLE_LIKE);
        likeRecordService.doLike(articleId, user.getUserId(),user.getNikeName(), DoLikeTypeEnum.ARTICLE_LIKE);
         return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param session
     * @param request
     * @param fileId
     * @return
     */
    @RequestMapping("/getUserDownloadInfo")
    @GobalAnnotation
    public Response getUserDownloadInfo(HttpSession session, HttpServletRequest request, String fileId){
        return getSuccessResponse(forumArticleService.getUserDownloadInfo(session, fileId));
    }

    /**
     * 已测试
     * @param session
     * @param request
     * @param response
     * @param fileId
     * @return
     */
    @RequestMapping("/attachmentDownload/{fileId}")
    public Response attachmentDownload(HttpSession session,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       @PathVariable("fileId") String fileId){
        attachmentService.attachmentDownload(session, request, response, fileId);
        return getSuccessResponse(null);
    }

    /**
     *已测试
     * @param session
     * @return
     */
    @RequestMapping("/loadBoard4Post")
    public Response loadBoard4Post(HttpSession session){
        List<ForumBoard> forumBoards = forumArticleService.loadBoard4Post(session);
        return getSuccessResponse(forumBoards);
    }

    /**
     * 测试困难 需要前端页面
     * @param session
     * @param cover
     * @param attachment
     * @param integral
     * @param title
     * @param pBoard
     * @param boardId
     * @param summary
     * @param editorType
     * @param content
     * @param markdownContent
     * @return
     */
    @RequestMapping("/postArticle")
    public Response postArticle(HttpSession session,
                                MultipartFile cover,
                                MultipartFile attachment,
                                Integer integral,
                                String title,
                                Integer pBoard,
                                Integer boardId,
                                String summary,//摘要
                                Integer editorType,//编辑器类型
                                String content,
                                String markdownContent
                                ){
        ForumArticle forumArticle = forumArticleService.postArticle(session, cover, attachment, integral, title, pBoard, boardId, summary, editorType, content, markdownContent);
        return getSuccessResponse(forumArticle.getArticleId());
    }
    @RequestMapping("/updateArticleDetail")
    public Response updateArticleDetail(HttpSession session, String articleId){
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", articleId));
        if(!user.getUserId().equals(forumArticle.getUserId())){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        return getSuccessResponse(null);
    }
    
    @RequestMapping("/updateArticle")
    public Response updateArticle(HttpSession session,
                                  MultipartFile cover,
                                  MultipartFile attachment,
                                  Integer integral,
                                  String articleId,
                                  String title,
                                  Integer pBoard,
                                  Integer boardId,
                                  String summary,
                                  Integer editorType,
                                  String content,
                                  String markdownContent,
                                  Integer attachmentType){
        ForumArticle forumArticle = forumArticleService.postArticle4Update(session, cover, attachment, integral, articleId, title, pBoard, boardId, summary, editorType, content, markdownContent,attachmentType);
        return getSuccessResponse(null);
    }

    /**
     *已测试
     * @param keyWords
     * @return
     */
    @RequestMapping("/research")
    public Response research(String keyWords){
        QueryWrapper<ForumArticle> query = new QueryWrapper<>();
        query.like("title", keyWords);
        List<ForumArticle> forumArticles = forumArticleMapper.selectList(query);
        return getSuccessResponse(forumArticles);

    }
}
