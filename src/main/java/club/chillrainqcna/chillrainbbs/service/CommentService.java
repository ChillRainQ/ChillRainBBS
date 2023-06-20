package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumComment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CommentService {
    List<ForumComment> loadComment(HttpSession session, String articleId, Integer commentPageNumber, Integer orderType);


    void topComment(String userId, Integer commentId, Integer topType);

    void postComment(HttpSession session, String articleId, Integer pCommentId, String content, MultipartFile image, String replayUserId);

    void delComments(String commentIds);
    void delComment(String commentId);

    void aduitComments(String commentIds);
    void aduitComment(String commentId);
}
