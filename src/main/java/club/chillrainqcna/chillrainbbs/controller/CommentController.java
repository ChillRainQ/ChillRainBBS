package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumComment;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.bean.SessionWebUser;
import club.chillrainqcna.chillrainbbs.entity.enums.DoLikeTypeEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.TopTypeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.ForumCommentMapper;
import club.chillrainqcna.chillrainbbs.service.CommentService;
import club.chillrainqcna.chillrainbbs.service.LikeRecordService;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import club.chillrainqcna.chillrainbbs.utils.StringUtil;
import club.chillrainqcna.chillrainbbs.utils.SystemSettingUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author ChillRain 2023 04 23
 */
@RequestMapping("/comment")
@RestController
public class CommentController extends BASEController {

    @Resource
    private CommentService commentService;
    @Resource
    private LikeRecordService likeRecordService;

    /**
     * 已测试
     * @param session
     * @param articleId
     * @param commentPageNumber
     * @param orderType
     * @return
     */
    @RequestMapping("/loadComment/{articleId}/{commentPageNumber}/{orderType}")
    public Response loadComment(HttpSession session, @PathVariable("articleId") String articleId, @PathVariable("commentPageNumber") Integer commentPageNumber, @PathVariable("orderType") Integer orderType){
        return getSuccessResponse(commentService.loadComment(session, articleId, commentPageNumber, orderType));
    }

    /**
     *已通过(?)
     * @param session
     * @param articleId
     * @param pCommentId
     * @param content
     * @param image
     * @param replayUserId
     * @return
     */
    @RequestMapping("/postComment")
    @GobalAnnotation(needLogin = true)
    public Response postComment(HttpSession session,
                                String articleId,
                                Integer pCommentId,
                                String content,
                                MultipartFile image,
                                String replayUserId){
        commentService.postComment(session,articleId, pCommentId, content, image, replayUserId);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param session
     * @param commentId
     * @return
     */
    @RequestMapping("/doLike")
    public Response doLike(HttpSession session, Integer commentId){
        SessionWebUser loginUserInfo = (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY);
        likeRecordService.doLike(String.valueOf(commentId),loginUserInfo.getUserId(), loginUserInfo.getNikeName(), DoLikeTypeEnum.COMMENT_LIKE);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param session
     * @param commentId
     * @param topType
     * @return
     */

    @RequestMapping("/topComment")
    public Response topComment(HttpSession session, Integer commentId, Integer topType){
        SessionWebUser loginUserInfo = (SessionWebUser)session.getAttribute(Constant.SESSION_USER_KEY);
        commentService.topComment(loginUserInfo.getUserId(), commentId, topType);
        return getSuccessResponse(null);
    }
}
