package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticle;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticleAttachment;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumComment;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.ForumArticleAttachmentMapper;
import club.chillrainqcna.chillrainbbs.mappers.ForumArticleMapper;
import club.chillrainqcna.chillrainbbs.mappers.ForumCommentMapper;
import club.chillrainqcna.chillrainbbs.service.AttachmentService;
import club.chillrainqcna.chillrainbbs.service.CommentService;
import club.chillrainqcna.chillrainbbs.service.ForumArticleService;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * @author ChillRain 2023 05 04
 */
@RestController
@RequestMapping("/adminForum")
public class AdminForumArticleController extends BASEController {
    @Resource
    private CommentService commentService;
    @Resource
    private ForumCommentMapper forumCommentMapper;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private ForumArticleAttachmentMapper forumArticleAttachmentMapper;
    @Resource
    private ForumArticleService forumArticleService;
    @Resource
    private ForumArticleMapper forumArticleMapper;

    /**
     * 已测试
     * @param boardId
     * @param userNickName
     * @param content
     * @param status
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadArticle")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response loadArticle(Integer boardId, //按板块查询
                                String userNickName, //按用户名查询
                                String content, //按文章内容查询
                                Integer status,//按文章状态查询
                                Integer pageNo){
        QueryWrapper<ForumArticle> query = new QueryWrapper<>();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        if(boardId != null){
            query.eq("board_id", boardId);
        }
        if(userNickName != null){
            query.eq("nick_name", userNickName);
        }
        if(content != null){
            query.like("content", content);
        }
        if(status != null){
            query.eq("status", status);
        }
        if(pageNo == null){
            pageNo = 1;
        }
        query.orderByDesc("post_time");
        Page<ForumArticle> page = new Page<>(pageNo, 10);
        Page<ForumArticle> articlePage = forumArticleMapper.selectPage(page, query);
        return getSuccessResponse(articlePage.getRecords());
    }

    /**
     *已测试
     * @param articleIds
     * @return
     */
    @RequestMapping("/delArticle")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response delArticle(String articleIds){//删除是一种标记删除 并不是从数据库中删除数据
        String[] ids = articleIds.split(",");
        forumArticleService.delArticleByIds(Arrays.asList(ids));
        return getSuccessResponse(null);
    }

    /**
     *已测试
     * @param articleId
     * @param pBoardId
     * @param boardId
     * @return
     */
    @RequestMapping("/updateArticleBoard")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response updateArticleBoard(String articleId, Integer pBoardId, Integer boardId){
        boardId = boardId == null ? 0 : boardId;
        forumArticleService.updateBoardInfo(articleId, pBoardId, boardId);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param articleId
     * @return
     */
    @RequestMapping("/getAttachment")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response getAttachment(String articleId){
        QueryWrapper<ForumArticleAttachment> query = new QueryWrapper<>();
        query.eq("article_id", articleId);
        List<ForumArticleAttachment> attachments = forumArticleAttachmentMapper.selectList(query);
        if(attachments == null || attachments.size() == 0){
            throw new ChillRainBBSException("附件不存在");
        }
        return getSuccessResponse(attachments.get(0));
    }

    /**
     *已测试
     * @param session
     * @param request
     * @param response
     * @param fileId
     * @return
     */
    @RequestMapping("/attachmentDownLoad")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response attachmentDownLoad(HttpSession session, HttpServletRequest request, HttpServletResponse response, String fileId){
        attachmentService.attachmentDownload(session, request, response, fileId);
        return getSuccessResponse(null);
    }

    /**
     *已测试
     * @param articleId
     * @param topType
     * @return
     */
    @RequestMapping("/topArticle")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response topArticle(String articleId, Integer topType){
        ForumArticle forumArticle = forumArticleMapper.selectOne(new QueryWrapper<ForumArticle>().eq("article_id", articleId));
        forumArticle.setTopType(topType);
        forumArticleMapper.updateById(forumArticle);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param articleIds
     * @return
     */
    @RequestMapping("/aduitArticle")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response aduitArticle(String articleIds){
        forumArticleService.aduitArticles(articleIds);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param userId
     * @param articleId
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadComment")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response loadComment(String userId, String articleId, Integer pageNo){
        QueryWrapper<ForumComment> query = new QueryWrapper<>();
        if (userId != null){
            query.eq("user_id", userId);
        }
        if(articleId != null){
            query.eq("article_id", articleId);
        }
        query.orderByDesc("post_time");
        pageNo = pageNo == null ? 1 : pageNo;
        Page<ForumComment> commentPage = new Page<>(pageNo, 10);
        Page<ForumComment> page = forumCommentMapper.selectPage(commentPage, query);
        return getSuccessResponse(page.getRecords());
    }

    /**
     * 已测试
     * @param userId
     * @param articleId
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadComment4Article")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response loadComment4Article(String userId, String articleId, Integer pageNo){
        QueryWrapper<ForumComment> query = new QueryWrapper<>();
        if (userId != null){
            query.eq("user_id", userId);
        }
        if(articleId != null){
            query.eq("article_id", articleId);
        }
        query.orderByDesc("post_time");
        query.eq("comment_id", 0);//只查一级评论
        pageNo = pageNo == null ? 1 : pageNo;
        Page<ForumComment> commentPage = new Page<>(pageNo, 10);
        Page<ForumComment> page = forumCommentMapper.selectPage(commentPage, query);
        return getSuccessResponse(page.getRecords());
    }

    /**
     * 已测试
     * @param commentIds
     * @return
     */
    @RequestMapping("/delComment")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response delComment(String commentIds){
        commentService.delComments(commentIds);
        return getSuccessResponse(null);
    }

    /**
     *
     * @param commentIds
     * @return
     */
    @RequestMapping("/aduitComment")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response aduitComment(String commentIds){
        commentService.aduitComments(commentIds);
        return getSuccessResponse(null );
    }


}
