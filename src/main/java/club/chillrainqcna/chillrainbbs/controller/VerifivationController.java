package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.service.VerifitionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 验证码Controller
 * @author ChillRain 2023 04 15
 */
@RestController
public class VerifivationController extends BASEController {
    @Resource
    private VerifitionService verifitionService;

    /**
     * 已测试
     * @param session
     * @param response
     * @param type
     * @throws IOException
     */
    @RequestMapping("/makePictureCode")
    public void checkCode(HttpSession session, HttpServletResponse response, Integer type) throws IOException {
        verifitionService.makePictureCode(response, session, type);
    }

    /**
     * 已测试
     * @param session
     * @param checkCode
     * @param email
     * @param type
     * @return
     */
    @RequestMapping("sendEmailCode")
    public Response sendEmailCode(HttpSession session, String checkCode, String email, Integer type){
        verifitionService.sendEmailCode(session, checkCode,  email, type);
        return getSuccessResponse(null);
    }

}
