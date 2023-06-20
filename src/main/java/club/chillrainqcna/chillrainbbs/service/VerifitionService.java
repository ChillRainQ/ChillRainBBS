package club.chillrainqcna.chillrainbbs.service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public interface VerifitionService {
    void makePictureCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException;

    void sendEmailCode(HttpSession session, String checkCode, String email, Integer type);

    void checkEmailCode(String email, String emailCode);

    boolean checkCode(HttpSession session, String checkCode);
}
