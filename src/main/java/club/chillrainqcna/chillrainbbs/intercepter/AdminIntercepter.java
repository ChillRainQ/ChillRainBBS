package club.chillrainqcna.chillrainbbs.intercepter;

import club.chillrainqcna.chillrainbbs.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ChillRain 2023 04 22
 */
@Component
public class AdminIntercepter implements HandlerInterceptor {
    @Resource
    private WebConfig webConfig;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler == null){
            return false;
        }
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        checkLogin();
        return true;


//        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void checkLogin() {

    }
}
