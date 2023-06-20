package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.*;
import club.chillrainqcna.chillrainbbs.entity.enums.*;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.*;
import club.chillrainqcna.chillrainbbs.service.UserService;
import club.chillrainqcna.chillrainbbs.service.VerifitionService;
import club.chillrainqcna.chillrainbbs.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author ChillRain 2023 04 16
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private JedisPool jedisPool;
    @Resource
    private WebConfig webConfig;
    @Resource
    private VerifitionService verifitionService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserMessageMapper userMessageMapper;
    @Resource
    private UserIntegralRecordMapper userIntegralRecordMapper;
    @Resource
    private FileUtil fileUtil;
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private ForumCommentMapper forumCommentMapper;

    /**
     * 用户注册
     * @param session
     * @param checkcode 图片验证码
     * @param email 邮箱
     * @param emailCode 邮箱验证码
     * @param nickName 昵称
     * @param password 密码 （前端需要进行MD5加密）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)//抛出异常则回滚
    public void register(HttpSession session, String checkcode, String email, String emailCode, String nickName, String password) {
        //参数校验
        if(NotNullUtil.isEmpty(email) || NotNullUtil.isEmpty(nickName) ||NotNullUtil.isEmpty(password)){
            throw new ChillRainBBSException("存在空参数");
        }
        if(!verifitionService.checkCode(session, checkcode)){
            throw new ChillRainBBSException("图片验证码错误");
        }
        //信息合法性检查
        QueryWrapper<UserInfo> user = new QueryWrapper<>();
        user.eq("email", email);
        if(userMapper.selectOne(user) != null){
            throw new ChillRainBBSException("邮箱已被使用");
        }
        user = new QueryWrapper<>();
        user.eq("nick_name", nickName);
        if(userMapper.selectOne(user) != null){
            throw new ChillRainBBSException("昵称已被使用");
        }
        verifitionService.checkEmailCode(email, emailCode);
        //写入新用户
        UserInfo userInfo = new UserInfo();
        String userId = StringUtil.getUserId();
        userInfo.setUserId(userId);
        userInfo.setEmail(email);
        userInfo.setNickName(nickName);
        userInfo.setJoinTime(new Date());
        userInfo.setStatus(1);
        userInfo.setPassword(StringUtil.encodeMD5(password));
        userInfo.setCurrentIntegral(0);
        userInfo.setTotalIntegral(0);
        userMapper.insert(userInfo);
        //注册完成 更新用户信息
        userIntegralOper(userId, UserIntegralOperTypeEnum.REGISTER, 1, 30);
        //发送消息
        UserMessage message = new UserMessage();
        message.setReceivedUserId(userId);
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus());
        message.setMessageType(MessageTypeEunm.SYS.getType());
        message.setCreateTime(new Date());
        message.setMessageContent(SystemSettingUtil.getSystemSetting().getRegister().getRegisterWelcomInfo());
        userMessageMapper.insert(message);
    }

    /**
     * 积分操作
     * @param userId 用户ID
     * @param operTypeEnum 操作类型
     * @param changeType 操作方式（1 加分， -1 减分）
     * @param integral 积分数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userIntegralOper(String userId, UserIntegralOperTypeEnum operTypeEnum, Integer changeType, Integer integral){
        Objects.requireNonNull(integral);
        integral = changeType * integral;
        if(integral == 0){
            return;
        }
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.eq("user_Id", userId);
        UserInfo user = userMapper.selectOne(query);
        if(user.getCurrentIntegral() + integral * changeType <= 0){
            integral = changeType * user.getCurrentIntegral();
        }

        //创建用户积分记录并插入数据库
        UserIntegralRecord userIntegralRecord = new UserIntegralRecord();
        userIntegralRecord.setUserId(userId);
        userIntegralRecord.setCreateTime(new Date());
        userIntegralRecord.setOperType(operTypeEnum.getOperType());
        userIntegralRecord.setIntegral(integral);
        userIntegralRecordMapper.insert(userIntegralRecord);
        //并发情况下 先去查在判断会有问题，他不是原子操作
        Integer count = userMapper.updateIntegral(userId, integral);
        if(count == 0){
            throw new ChillRainBBSException("更新用户" + user.getNickName() + "积分失败, 操作将回滚");
        }
    }

    /**
     * 用户登录
     * @param session
     * @param checkCode 验证码
     * @param email 邮箱
     * @param password 密码（前端需要进行MD5加密）
     * @param ip 用户的登录ip
     * @return
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionWebUser login(HttpSession session,String checkCode, String email, String password, String ip) {
        Jedis jedis = jedisPool.getResource();
        String jsonUser = jedis.get(Constant.LOGIN_USER_INFO + ip);
        UserInfo user = null;
        if(!NotNullUtil.isEmpty(jsonUser)){
            System.out.println("=========> login by redis");
            user = JsonUtil.json2object(jsonUser, UserInfo.class);
        }else{
            //参数校验
            if(!verifitionService.checkCode(session, checkCode)){
                throw new ChillRainBBSException("验证码错误");
            }
            if(NotNullUtil.isEmpty(email) || NotNullUtil.isEmpty(password)){
                throw new ChillRainBBSException("登录参数错误");
            }
            System.out.println("=========> login by mysql");
            QueryWrapper query = new QueryWrapper();
            query.eq("email", email);
            user = userMapper.selectOne(query);
            //此处MD5需要更改
            if(!user.getPassword().equals(password) || user == null){
                throw new ChillRainBBSException("账号或密码错误");
            }
            if(user.getStatus() == UserStatusEnum.BAN.getStatus()){
                throw new ChillRainBBSException("此账户已禁用");
            }
        }

        //编写已登录用户信息（登陆时间， 登录ip， 登录地点）
        UserInfo loginUser = new UserInfo();
        String ipAddress = getIAddress(ip);
        loginUser.setLastLoginTime(new Date());
        loginUser.setLastLoginIp(ip);
        loginUser.setLastLoginIpAddress(ipAddress);
        String isAdmin;
        if(user.getEmail().equals(webConfig.getAdminEmail())){
            isAdmin = "true";
        }else {
            isAdmin = "false";
        }
        //更新已登录用户信息
        QueryWrapper query = new QueryWrapper();
        query.eq("email", email);
        userMapper.update(loginUser, query);
        //设置七天免登陆
        jedis.set(Constant.LOGIN_USER_INFO + ip, JsonUtil.object2Json(user));
        jedis.expire(Constant.LOGIN_USER_INFO + ip, 7 * 24 * 60 * 60);
        jedis.close();
        //返回用户信息
        SessionWebUser sessionWebUser = new SessionWebUser();
        sessionWebUser.setUserId(user.getUserId());
        sessionWebUser.setNikeName(user.getNickName());
        sessionWebUser.setProvince(ipAddress);
        sessionWebUser.setIsAdmin(isAdmin);
        return sessionWebUser;
    }

    /**
     * 重置密码
     * @param session
     * @param password 密码
     * @param email 邮箱
     * @param checkCode 图片验证码
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(HttpSession session, String password, String email, String checkCode) {
        if(NotNullUtil.isEmpty(password) || NotNullUtil.isEmpty(email)){
            throw new ChillRainBBSException("参数异常");
        }
        if(!verifitionService.checkCode(session, checkCode)){
            throw new ChillRainBBSException("验证码错误");
        }
        UserInfo user = new UserInfo();
        QueryWrapper query = new QueryWrapper();
        query.eq("email", email);
        user.setPassword(StringUtil.encodeMD5(password));
        user.setEmail(email);
        int count = userMapper.update(user, query);
        if(count == 0){
            throw new ChillRainBBSException("用户不存在");
        }
    }

    /**
     * 登出
     * @param session
     * @param request
     */

    @Override
    public void logout(HttpSession session, HttpServletRequest request) {
        session.invalidate();
        String ip = IPUtil.getIp(request);
        Jedis jedis = jedisPool.getResource();
        jedis.del(Constant.LOGIN_USER_INFO + ip);
        jedis.close();
    }

    @Override
    public void updateUserInfo(UserInfo userInfo, MultipartFile avatar) {
        userMapper.update(userInfo, new QueryWrapper<UserInfo>().eq("user_id", userInfo.getUserId()));
        if(avatar != null){
            fileUtil.uploadFile2Loacl(avatar, userInfo.getUserId(), FileUploadTypeEnum.AVATAR);
        }
    }

    /**
     * 封禁
     * @param status
     * @param userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Integer status, String userId) {
        if(UserStatusEnum.BAN.getStatus().equals(status)){//修改作者的作品状态
            ForumArticle article = new ForumArticle();
            article.setStatus(ArticleStatusEnum.DEL.getStatus());
            forumArticleMapper.update(article, new QueryWrapper<ForumArticle>().eq("user_id", userId));
            ForumComment comment = new ForumComment();
            comment.setStatus(CommentStatusEnum.DEL.getStatus());
            forumCommentMapper.update(comment, new QueryWrapper<ForumComment>().eq("user_id", userId));
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setStatus(UserStatusEnum.getByStatus(status).getStatus());
        userMapper.update(userInfo, new QueryWrapper<UserInfo>().eq("user_id", userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Integer integral, String userId, String content) {
        UserMessage message = new UserMessage();
        message.setStatus(MessageStatusEnum.NOT_READ.getStatus())
                .setMessageType(MessageTypeEunm.SYS.getType())
                .setReceivedUserId(userId)
                .setMessageContent(content)
                .setCreateTime(new Date());
        userMessageMapper.insert(message);
        if(integral != null){//发送积分
            userMapper.updateIntegral(userId, integral);
        }
    }

    /**
     * 已测试
     * 获取用户登录位置（精确到省份）
     * @param ip
     * @return
     */

    private String getIAddress(String ip){
        String url = "http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=";
        String site = null;
        String responseJson = HttpUtil.HTTPGet(url + ip);
        if(responseJson == null){
            return "未知位置";
        }
        Map<String, String> addressInfo = JsonUtil.json2object(responseJson, Map.class);
        site = addressInfo.get("pro");
        if(site == "") site = "未知位置";
        return site;
    }


}
