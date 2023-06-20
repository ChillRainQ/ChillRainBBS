package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.service.ForumService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ChillRain 2023 04 19
 */
@RestController
@RequestMapping("/board")
public class ForumController extends BASEController {
    @Resource
    private ForumService forumService;

    /**
     * 已测试
     * @return
     */
    @RequestMapping("/load")
    public Response load(){
        return getSuccessResponse(forumService.getForumBoard(null));
    }
}
