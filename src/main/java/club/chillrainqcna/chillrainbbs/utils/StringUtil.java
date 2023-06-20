package club.chillrainqcna.chillrainbbs.utils;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumComment;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * @author ChillRain 2023 04 16
 */
public class StringUtil {
    public static final String getEmailCode(){
        return RandomStringUtils.random(5,true, true);
    }
    public static final String getUserId(){
        return RandomStringUtils.random(10, false, true);
    }
    public static final String encodeMD5(String str){
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }
    public static final String getsuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }
    public static final String getRandomString(Integer length){
        return RandomStringUtils.random(length, true, true);
    }
    public static final String getRandomNumber(Integer length){
        return RandomStringUtils.random(length, false, true);
    }
    public static final String content2Html(String content){
        if(NotNullUtil.isEmpty(content)){
            return content;
        }
        content = content.replace("<", "&lt");
        content = content.replace(" ", "&nbsp");
        content = content.replace("\n", "<br>");
        return content;
    }
    public static final String getFileName(String fileName){
        return fileName.substring(0,fileName.lastIndexOf("."));
    }
}
