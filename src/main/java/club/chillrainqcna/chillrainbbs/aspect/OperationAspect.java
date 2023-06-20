package club.chillrainqcna.chillrainbbs.aspect;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.SessionWebUser;
import club.chillrainqcna.chillrainbbs.entity.bean.UserInfo;
import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.service.UserService;
import club.chillrainqcna.chillrainbbs.utils.IPUtil;
import club.chillrainqcna.chillrainbbs.utils.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author ChillRain 2023 04 18
 * AOP制作拦截器
 */
@Component
@Aspect
public class OperationAspect {
    @Resource
    private WebConfig webConfig;
    @Resource
    private UserService userService;
    @Resource
    private JedisPool jedisPool;
    @Pointcut("@annotation(club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation)")
    private void registerInterceptor(){

    }
    @Around("registerInterceptor()")
    public Object interceptorDo(ProceedingJoinPoint point){
        try{
            Object target = point.getTarget();//从切点获取目标
            Object[] args = point.getArgs();//从切点获取参数
            String methodName = point.getSignature().getName();//从切点获取方法名
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            GobalAnnotation annotation = method.getAnnotation(GobalAnnotation.class);
            if(annotation == null){
                return null;
            }

            if(annotation.needLogin()){
                needLogin();
            }
            if(annotation.needAdmin()){
                needAdmin();
            }
            Object proceed = point.proceed();
            return proceed;
        }catch (Exception e){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_404);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
//        return null;
    }

    private void needAdmin(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionWebUser userSession = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        if(userSession.getIsAdmin().equals("false")){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_404);
        }else {
            return;
        }
    }

    /**
     * 已测试
     */
    private void needLogin(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionWebUser userSession = (SessionWebUser) session.getAttribute(Constant.SESSION_USER_KEY);
        if(userSession == null){
            String ip = IPUtil.getIp(request);
            Jedis jedis = jedisPool.getResource();
            String jsonUser = jedis.get(Constant.LOGIN_USER_INFO + ip);
            if(jsonUser == null){
                throw new ChillRainBBSException("未登录");
            }
            UserInfo user = JsonUtil.json2object(jsonUser, UserInfo.class);
            jedis.set(Constant.LOGIN_USER_INFO + ip, JsonUtil.object2Json(user));
            jedis.expire(Constant.LOGIN_USER_INFO + ip, 7 * 24 * 60 * 60);
            jedis.close();
            SessionWebUser loginUser = new SessionWebUser();
            loginUser.setUserId(user.getUserId());
            loginUser.setNikeName(user.getNickName());
            String isAdmin = null;
            if(user.getEmail().equals(webConfig.getAdminEmail())){
                isAdmin = "true";
            }else {
//                isAdmin = "false";
                isAdmin = "true";
            }
            loginUser.setIsAdmin(isAdmin);
            loginUser.setProvince(IPUtil.getIAddress(ip));
            session.setAttribute(Constant.SESSION_USER_KEY, loginUser);
        }
    }
}
