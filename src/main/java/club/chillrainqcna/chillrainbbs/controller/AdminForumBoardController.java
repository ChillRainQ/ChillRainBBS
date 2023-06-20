package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.annotation.GobalAnnotation;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumBoard;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.dto.FileUploadDto;
import club.chillrainqcna.chillrainbbs.entity.enums.FileUploadTypeEnum;
import club.chillrainqcna.chillrainbbs.mappers.ForumMapper;
import club.chillrainqcna.chillrainbbs.service.ForumService;
import club.chillrainqcna.chillrainbbs.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author ChillRain 2023 05 04
 */
@RestController
@RequestMapping("/adminBoard")
public class AdminForumBoardController extends BASEController {
    @Resource
    private FileUtil fileUtil;
    @Resource
    private ForumService forumService;
    @Resource
    private ForumMapper forumMapper;

    /**
     *已测试
     * @return
     */
    @RequestMapping("/loadBoard")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response loadBoard(){
        return getSuccessResponse(forumService.getForumBoard(null));
    }

    /**
     * 已测试
     * @param boardId
     * @param pBoardId
     * @param boardName
     * @param boardDesc
     * @param postType
     * @param cover
     * @return
     */
    @RequestMapping("/saveBoard")
    @GobalAnnotation(needLogin = true, needAdmin = true)
    public Response saveBoard(Integer boardId,
                              Integer pBoardId,
                              String boardName,
                              String boardDesc,
                              Integer postType,
                              MultipartFile cover){
        //先封装
        ForumBoard forumBoard = new ForumBoard();
        forumBoard.setPBoardId(pBoardId)
                .setBoardDesc(boardDesc)
                .setBoardId(boardId)
                .setBoardName(boardName)
                .setPostType(postType);
        if(cover != null){
            FileUploadDto fileUploadDto = fileUtil.uploadFile2Loacl(cover, Constant.FILE_FOLDER_IMAGE, FileUploadTypeEnum.ARTICLE_COVER);
            forumBoard.setCover(fileUploadDto.getLocalPath());
        }
        forumService.saveForumBoard(forumBoard);
        return getSuccessResponse(null);
    }

    /**
     * 已测试
     * @param boardId
     * @return
     */
    @RequestMapping("/delBoard")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response delBoard(Integer boardId){
        forumMapper.delete(new QueryWrapper<ForumBoard>().eq("board_id", boardId));
        return getSuccessResponse(null);
    }

    /**
     *已测试
     * @param boardIds
     * @return
     */
    @RequestMapping("/changeSort")
    @GobalAnnotation(needAdmin = true, needLogin = true)
    public Response changeSort(String boardIds){
        forumService.changeSort(boardIds);
        return getSuccessResponse(null);
    }
}
