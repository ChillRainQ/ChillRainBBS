package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.bean.UserInfo;
import club.chillrainqcna.chillrainbbs.mappers.UserMapper;
import club.chillrainqcna.chillrainbbs.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ChillRain 2023 05 05
 */
@RestController
@RequestMapping("/adminUser")
public class AdminUserController extends BASEController {
    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    /**
     * 已通过
     * @param sex
     * @param status
     * @param pageNo
     * @param nikeName
     * @return
     */
    @RequestMapping("/loadUserList")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response loadUserList(Integer sex, Integer status, Integer pageNo, String nikeName){
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        pageNo = pageNo == null ? 1 : pageNo;
        query.orderByDesc("join_time");
        if(sex != null){
            query.eq("sex", sex);
        }
        if(status != null){
            query.eq("status", status);
        }
        if(nikeName != null){
            query.eq("nick_name", nikeName);
        }
        Page<UserInfo> page = new Page<>(pageNo, 10);
        Page<UserInfo> userInfoPage = userMapper.selectPage(page, query);
        return getSuccessResponse(userInfoPage.getRecords());
    }

    /**
     *已测试
     * @param status
     * @param userId
     * @return
     */
    @RequestMapping("/updateUserStatus")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response updateUserStatus(Integer status, String userId){
        userService.updateUserStatus(status, userId);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param integral
     * @param userId
     * @param content
     * @return
     */
    @RequestMapping("/sendMessage")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response sendMessage(Integer integral, String userId, String content){
        userService.sendMessage(integral, userId, content);
        return getSuccessResponse(null);
    }
}
