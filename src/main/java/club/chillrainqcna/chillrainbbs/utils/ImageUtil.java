package club.chillrainqcna.chillrainbbs.utils;

import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.config.root.AppConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.enums.DateFormatEnum;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ChillRain 2023 05 01
 */
@Component
public class ImageUtil {
    @Resource
    private WebConfig webConfig;
    @Resource
    private AppConfig appConfig;
    public static Boolean createThumbnail(File file, int thumbnailWidth, int thumbnailHeight, File targetFile){
        try{
            BufferedImage src = ImageIO.read(file);
            int srcHeight = src.getHeight();
            int srcWidth = src.getWidth();
            if(srcWidth < thumbnailWidth){//图片的宽高小于要求
                return false;
            }
            int height = srcHeight;
            if(srcWidth > thumbnailWidth){//宽高大于要求 等比例缩小
                height = thumbnailWidth * srcHeight / srcWidth;
            }else{
                thumbnailWidth = srcWidth;
                height = srcHeight;
            }
            //缩略图生成
            BufferedImage dst = new BufferedImage(thumbnailWidth, height, BufferedImage.TYPE_INT_BGR);
            Image scaleImage = src.getScaledInstance(thumbnailWidth, height, Image.SCALE_SMOOTH);
            Graphics2D g = dst.createGraphics();
            g.drawImage(scaleImage, 0, 0, thumbnailWidth, height, null);
            g.dispose();
            int resultH = dst.getHeight();
            if(resultH > thumbnailHeight){
                resultH = thumbnailHeight;
                dst.getSubimage(0, 0, thumbnailWidth, resultH);
            }
            ImageIO.write(dst, "JPEG", targetFile);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return false;
    }
    public String resetImageHtml(String html){
        String month = DateUtil.format(new Date(), DateFormatEnum.YYYYMM.getFormat());
        List<String> imageList = getImageList(html);
        for (String s : imageList) {
            resetImage(s, month);
        }
        return month;
    }

    /**
     * 重设定temp下的文件到正式文件夹
     * @param imgPath
     * @param month
     * @return
     */
    private String resetImage(String imgPath, String month){
        if(NotNullUtil.isEmpty(imgPath) || !imgPath.contains(Constant.FILE_FOLDER_TEMP_2)){
            return imgPath;
        }
        imgPath = imgPath.replace(Constant.READ_IMAGE_PATH, "");
        if(NotNullUtil.isEmpty(month)){
            month = DateUtil.format(new Date(), DateFormatEnum.YYYYMM.getFormat());
        }
        String imgFileName = month + "/" + imgPath.substring(imgPath.lastIndexOf("/" + 1));
        File target = new File(webConfig.getProjectFolder() + Constant.FILE_FOLDER_IMAGE + imgFileName);
        File old = new File(webConfig.getProjectFolder() + Constant.FILE_FOLDER_TEMP + imgFileName);
//        old.delete();
        try{
            FileUtils.copyFile(old, target);
        } catch (IOException e) {
            System.out.println("复制图片失败");
            return imgPath;
        }
        return imgFileName;
    }

    /**
     * 正则解析img标签
     * @param html
     * @return
     */
    private List<String> getImageList(String html){
        List<String> imageList = new ArrayList<String>();
        String regEx_img = "(<img.*src\\s*=\\s*(.*?)[^>]*?>)";
        Pattern compile = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(html);
        while(matcher.find()){
            String group = matcher.group();
            Matcher matcher1 = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(group);
            while(matcher1.find()){
                String imageUrl = matcher1.group(1);
                imageList.add(imageUrl);
            }
        }
        return imageList;
    }
}
