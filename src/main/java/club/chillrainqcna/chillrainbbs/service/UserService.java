package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.bean.SessionWebUser;
import club.chillrainqcna.chillrainbbs.entity.bean.UserInfo;
import club.chillrainqcna.chillrainbbs.entity.enums.UserIntegralOperTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface UserService {
    void register(HttpSession session, String checkcode, String email, String emailCode, String nickName, String password);
    void userIntegralOper(String userId, UserIntegralOperTypeEnum operTypeEnum, Integer changeType, Integer integral);

    SessionWebUser login(HttpSession session, String checkCode, String email, String password, String ip);

    void resetPassword(HttpSession session, String password, String email, String checkCode);

    void logout(HttpSession session, HttpServletRequest request);

    void updateUserInfo(UserInfo userInfo, MultipartFile avatar);

    void updateUserStatus(Integer status, String userId);

    void sendMessage(Integer integral, String userId, String content);
}
