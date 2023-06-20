package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.*;
import club.chillrainqcna.chillrainbbs.entity.dto.UserMessageCountDto;
import club.chillrainqcna.chillrainbbs.entity.enums.ArticleStatusEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.MessageTypeEunm;
import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;
import club.chillrainqcna.chillrainbbs.entity.vo.UserInfoVO;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.*;
import club.chillrainqcna.chillrainbbs.service.UserMessageService;
import club.chillrainqcna.chillrainbbs.service.UserService;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author ChillRain 2023 05 03
 */
@RestController
@RequestMapping("/ucenter")
public class UserCenterController extends BASEController {
    @Resource
    private UserMessageMapper userMessageMapper;
    @Resource
    private UserMessageService userMessageService;
    @Resource
    private UserIntegralRecordMapper userIntegralRecordMapper;
    @Resource
    private LikeRecordMapper likeRecordMapper;
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    /**
     * 已测试
     * @param userId
     * @return
     */
    @RequestMapping("/getUserInfo")
    public Response getUserInfo(String userId){//获取对应的用户信息
        if(NotNullUtil.isEmpty(userId)){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        UserInfo userInfo = userMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_id", userId));
        if(userInfo == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_404);
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        //复制信息
        userInfoVO.setUserId(userInfo.getUserId());
        userInfoVO.setStatus(userInfo.getStatus());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setCurrentIntegral(userInfo.getCurrentIntegral());
        userInfoVO.setLastLoginTime(userInfo.getLastLoginTime());
        userInfoVO.setPersonDescription(userInfo.getPersonDescription());
        userInfoVO.setJoinTime(userInfo.getJoinTime());
        userInfoVO.setSex(userInfo.getSex());
        userInfoVO.setLastLoginIpAddress(userInfo.getLastLoginIpAddress());
        //查询文章数 和点赞数
        Integer postCount = forumArticleMapper.selectCount(new QueryWrapper<ForumArticle>().eq("user_id", userId).eq("status", ArticleStatusEnum.AUDIT.getStatus()));
        Integer likeCount = likeRecordMapper.selectCount(new QueryWrapper<LikeRecord>().eq("user_id", userId));
        userInfoVO.setLikeCount(likeCount);
        userInfoVO.setPostCount(postCount);
        return getSuccessResponse(userInfoVO);
    }

    /**
     * 已测试
     * @param session
     * @param userId
     * @param type
     * @return
     */
    @RequestMapping("/loadUserArticle")
    public Response loadUserArticle(HttpSession session, String userId, Integer type){//获取用户的文章信息

        if(NotNullUtil.isEmpty(userId)){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        UserInfo userInfo = userMapper.selectOne(new QueryWrapper<UserInfo>().eq("user_id", userId));
        if(userInfo == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_404);
        }
        QueryWrapper<ForumArticle> query = new QueryWrapper<>();
        query.orderByDesc("post_time");
        if(type == 0){
            query.eq("user_id", userId);
        }
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        if(user.getUserId().equals(userId)){//是本人 可以查看未审核文章

        }else {
            query.eq("status", ArticleStatusEnum.AUDIT.getStatus());
        }
        Page<ForumArticle> page = new Page<>(1,5);

        Page<ForumArticle> page1 = forumArticleMapper.selectPage(page, query);
        return getSuccessResponse(page1.getRecords());
    }

    /**
     *已测试
     * @param session
     * @param sex
     * @param personDesc
     * @param avater
     * @return
     */

    @RequestMapping("/updateUserInfo")
    @Transactional(rollbackFor = Exception.class)
    public Response updateUserInfo(HttpSession session,
                                   Integer sex,
                                   String personDesc,
                                   MultipartFile avater){
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        UserInfo updateInfo = new UserInfo();
        updateInfo.setUserId(user.getUserId());
        if(sex != null) updateInfo.setSex(sex);
        if(personDesc != null)updateInfo.setPersonDescription(personDesc);
        if(avater != null) updateInfo.setUserId(user.getUserId());
        userService.updateUserInfo(updateInfo, avater);
        return getSuccessResponse(null);
    }

    /**
     *已测试
     * @param session
     * @param pageNumber
     * @return
     */
    @RequestMapping("/loadUserIntegralRecord")
    public Response loadUserIntegralRecord(HttpSession session, Integer pageNumber){
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        Page<UserIntegralRecord> page = new Page<>(pageNumber, 10);
        QueryWrapper<UserIntegralRecord> query = new QueryWrapper<>();
        query.eq("user_id", user.getUserId());
        Page<UserIntegralRecord> selectPage = userIntegralRecordMapper.selectPage(page, query);
        return getSuccessResponse(selectPage.getRecords());
    }

    /**
     * 已通过
     * @param session
     * @return
     */
    @RequestMapping("/getMessageCount")
    public Response getMessageCount(HttpSession session){
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        UserMessageCountDto userMessageCount = userMessageService.getUserMessageCount(user.getUserId());
        return getSuccessResponse(userMessageCount);
    }

    /**
     *已测试
     * @param session
     * @param code
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadMessageList")
    public Response loadMessageList(HttpSession session, String code, Integer pageNo){
        SessionWebUser user = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        MessageTypeEunm type = MessageTypeEunm.getByCode(code);
        if(type == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        QueryWrapper<UserMessage> query = new QueryWrapper<>();
        pageNo = pageNo == null ? 1 : pageNo;
        Page<UserMessage> page = new Page<>(pageNo, 10);
        query.orderByDesc("message_id");
        query.eq("message_type", type.getType());
        query.eq("received_user_id", user.getUserId());
        Page<UserMessage> userMessagePage = userMessageMapper.selectPage(page, query);
        //设置已读
        if(page != null || pageNo == 1){
            userMessageService.readMessageByType(user.getUserId(), type.getType());
        }
        return getSuccessResponse(userMessagePage.getRecords());
    }
}
