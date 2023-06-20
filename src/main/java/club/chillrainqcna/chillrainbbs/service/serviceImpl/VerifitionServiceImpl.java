package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.UserInfo;

import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.UserMapper;
import club.chillrainqcna.chillrainbbs.service.VerifitionService;
import club.chillrainqcna.chillrainbbs.utils.ImageCodeUtil;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import club.chillrainqcna.chillrainbbs.utils.StringUtil;
import club.chillrainqcna.chillrainbbs.utils.SystemSettingUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * @author ChillRain 2023 04 16
 */
@Service
public class VerifitionServiceImpl implements VerifitionService {
    @Resource
    private JedisPool jedisPool;
    @Resource
    private WebConfig webConfig;
    @Resource
    private JavaMailSender sender;
    @Resource
    private UserMapper userMapper;
    /**
     * 图片验证码生成 (防机器人)
     * @param response
     * @param session
     * @param type
     * @throws IOException
     */

    @Override
    public void makePictureCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        ImageCodeUtil util = new ImageCodeUtil(130, 38, 4, 10);
        //设置响应头
//        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");//设置请求是否缓存 不缓存则为实时响应
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = util.getCode();//创建验证码二进制流
        //登录思维
        if(type == null|| type == 0){
            session.setAttribute(Constant.CHECK_CODE_KEY, code);
        }else {
            //邮箱注册的图片验证码
            session.setAttribute(Constant.CHECK_CODE_KEY_EMAIL, code);
        }
        util.write(response.getOutputStream());
    }
    public boolean checkCode(HttpSession session, String checkCode){
        String code = (String)session.getAttribute(Constant.CHECK_CODE_KEY);
        if(NotNullUtil.isEmpty(code) || NotNullUtil.isEmpty(checkCode)){
            throw new ChillRainBBSException("验证码错误");
        }
        try{
            if(code.equalsIgnoreCase(checkCode)){
                return true;
            }else{
                return false;
            }
        }finally {
            session.removeAttribute(Constant.CHECK_CODE_KEY);
        }
    }

    /**
     * 邮箱验证码生成
     * @param email
     * @param type
     */

    @Override
    public void sendEmailCode(HttpSession session, String checkCode, String email, Integer type) {
        checkCode(session, checkCode);
        if(type == 0){
            //发送邮件前先查看邮箱是否注册
            QueryWrapper<UserInfo> userinfo = new QueryWrapper<>();
            userinfo.eq("email", email);
            UserInfo user = userMapper.selectOne(userinfo);
            if(user != null){//用户存在
                throw new ChillRainBBSException("用户已存在");
            }
        }

        //用户不存在 生成邮箱验证码并发邮件
        String code = StringUtil.getEmailCode();
        //验证码数据写入redis缓存并设置过期时间
        Jedis jedis = jedisPool.getResource();
        String codeInJedis = jedis.get("Email-" + email);
        if(codeInJedis != null){//存在未过期验证码 删了 发新的
            jedis.del("Email-" + email);
            System.out.println("======> email code has been reset!");
        }
        //没有验证码 发个新的
        jedis.set("Email-" + email, code);
        jedis.expire("Email-" + email,10 * 60);
        jedis.close();
        sendEmailCodeDo(email, code);//邮件发送
    }

    /**
     * 邮箱验证码验证
     * @param email
     * @param emailCode
     */

    @Override
    public void checkEmailCode(String email, String emailCode) {
        Jedis jedis = jedisPool.getResource();
        String code = jedis.get("Email-" + email);
        if(!code.equalsIgnoreCase(emailCode)){
            throw new ChillRainBBSException("邮箱验证码不正确");
        }else{
            jedis.del("Email-" + email);
        }
        jedis.close();
    }

    /**
     * 邮箱验证码发送
     * @param email
     * @param code
     */
    private void sendEmailCodeDo(String email, String code){
        try {
            MimeMessage message = sender.createMimeMessage();//发送器
            MimeMessageHelper helper = new MimeMessageHelper(message);//编辑器
            /*
            邮件编辑
             */
//            helper.setSubject("邮箱验证");
//            helper.setText("邮箱验证码为：" + code);
            helper.setSubject(SystemSettingUtil.getSystemSetting().getEmail().getEmailTitle());
            helper.setText(SystemSettingUtil.getSystemSetting().getEmail().getEmailContent().replace("%s", code));
            helper.setSentDate(new Date());
            helper.setTo(email);
            helper.setFrom(webConfig.getUsername());
            sender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
