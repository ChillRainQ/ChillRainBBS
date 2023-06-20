package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticleAttachment;
import club.chillrainqcna.chillrainbbs.entity.bean.SessionWebUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface AttachmentService {
    ForumArticleAttachment downloadAttachment(String fileId, SessionWebUser sessionWebUser);

    void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, String fileId);
}
