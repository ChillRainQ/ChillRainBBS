package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.systemSetting.*;
import club.chillrainqcna.chillrainbbs.service.SystemSettingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author ChillRain 2023 05 04
 */
@RestController
@RequestMapping("/adminSetting")
public class AdminSysSettingController extends BASEController {
    @Resource
    private SystemSettingService systemSettingService;

    /**
     * 已测试
     * @return
     * @throws IntrospectionException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    @RequestMapping("/getSetting")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response getSetting() throws IntrospectionException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return getSuccessResponse(systemSettingService.refreshCache());
    }

    /**
     * API不存在
     * @param audit
     * @param register
     * @param post
     * @param like
     * @param comment
     * @param email
     * @return
     * @throws IntrospectionException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    @RequestMapping("/saveSetting")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response saveSetting(SystemSetting4AuditDto audit,
                                SystemSetting4Register register,
                                SystemSetting4PostDto post,
                                SystemSetting4LikeDto like,
                                SystemSetting4CommentDto comment,
                                SystemSetting4EmailDto email) throws IntrospectionException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        SystemSetting systemSetting = new SystemSetting();
        systemSetting.setAudit(audit);
        systemSetting.setPost(post);
        systemSetting.setLike(like);
        systemSetting.setEmail(email);
        systemSetting.setRegister(register);
        systemSetting.setComment(comment);
        systemSettingService.saveSystemSetting(systemSetting);
        return getSuccessResponse(null);
    }
}
