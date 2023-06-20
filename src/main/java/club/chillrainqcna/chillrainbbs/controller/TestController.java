package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.mappers.UserMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ChillRain 2023 04 15
 */
@RestController
public class TestController extends BASEController {
    @Resource
    private UserMapper userMapper;
    @RequestMapping("/test")
    public Response test(){
        userMapper.updateIntegral("userId", 0);
        return getSuccessResponse(null);
//        return "Hello BBS";
    }

}
