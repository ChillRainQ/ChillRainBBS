package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticle;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumBoard;
import club.chillrainqcna.chillrainbbs.entity.enums.DoLikeTypeEnum;
import club.chillrainqcna.chillrainbbs.entity.vo.UserDownloadVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ForumArticleService {
    ForumArticle readArticle(HttpSession session, String articleId);
    List<ForumArticle> loadArticle(Integer boardId, Integer pBoardId, Integer orderType, Integer pageNo);
//    void doLike(String objectId, String UserId, String nickName);

    void doLike(String objectId, String UserId, String nickName, DoLikeTypeEnum typeEnum);

    UserDownloadVO getUserDownloadInfo(HttpSession session, String fileId);

    List<ForumBoard> loadBoard4Post(HttpSession session);

    ForumArticle postArticle(HttpSession session, MultipartFile cover, MultipartFile attachment, Integer integral, String title, Integer pBoard, Integer boardId, String summary, Integer editorType, String content, String markdownContent);

    ForumArticle postArticle4Update(HttpSession session, MultipartFile cover, MultipartFile attachment, Integer integral, String articleId, String title, Integer pBoard, Integer boardId, String summary, Integer editorType, String content, String markdownContent, Integer attachmentType);

    void delArticleByIds(List<String> list);
    void delArticleById(String is);

    void updateBoardInfo(String articleId, Integer pBoardId, Integer boardId);

    void aduitArticles(String articleIds);

    void aduitArticle(String articleId);
//    void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, String fileId);
}
