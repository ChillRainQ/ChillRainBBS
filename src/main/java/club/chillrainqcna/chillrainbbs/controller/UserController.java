package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.bean.SessionWebUser;
import club.chillrainqcna.chillrainbbs.service.UserService;
import club.chillrainqcna.chillrainbbs.utils.IPUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author ChillRain 2023 04 16
 */
@RestController
public class UserController extends BASEController {
    @Resource
    private UserService userService;

    /**
     * 已测试
     * @param session
     * @param checkCode
     * @param email
     * @param emailCode
     * @param nickName
     * @param password
     * @return
     */
    @RequestMapping("/register")
    public Response register(HttpSession session, String checkCode, String email, String emailCode, String nickName, String password){
        userService.register(session, checkCode, email, emailCode, nickName, password);
        return getSuccessResponse("注册成功");
    }

    /**
     * 已测试
     * @param session
     * @param checkCode
     * @param email
     * @param password
     * @param request
     * @return
     */
    @RequestMapping("/login/{checkCode}/{email}/{password}")
    public Response login(HttpSession session, @PathVariable("checkCode") String checkCode, @PathVariable("email") String email, @PathVariable("password") String password, HttpServletRequest request){
        String ip = IPUtil.getIp(request);
        SessionWebUser sessionWebUser = userService.login(session, checkCode, email, password, ip);
        session.setAttribute(Constant.SESSION_USER_KEY, sessionWebUser);
        return getSuccessResponse("登陆成功");
    }

    /**
     * 已测试
     * @param session
     * @return
     */
    @RequestMapping("/getUserInfo")
    @GobalAnnotation(needLogin = true)
    public Response getUserInfo(HttpSession session){
        return getSuccessResponse(session.getAttribute(Constant.SESSION_USER_KEY));
    }

    /**
     * 已测试
     * @param session
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    @GobalAnnotation(needLogin = true)
    public Response logout(HttpSession session, HttpServletRequest request){
        userService.logout(session ,request);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param session
     * @param password
     * @param email
     * @param checkCode
     * @return
     */
    @RequestMapping("/resetPassword")
    @GobalAnnotation(needLogin = true)
    public Response resetPassword(HttpSession session, String password, String email, String checkCode){
        userService.resetPassword(session, password, email, checkCode);
        return getSuccessResponse(null);
    }
}
