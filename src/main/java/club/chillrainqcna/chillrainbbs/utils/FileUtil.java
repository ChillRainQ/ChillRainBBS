package club.chillrainqcna.chillrainbbs.utils;

import club.chillrainqcna.chillrainbbs.config.root.AppConfig;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.dto.FileUploadDto;
import club.chillrainqcna.chillrainbbs.entity.enums.DateFormatEnum;
import club.chillrainqcna.chillrainbbs.entity.enums.FileUploadTypeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * @author ChillRain 2023 04 29
 */
@Component
public class FileUtil {
    @Resource
    private ImageUtil imageUtil;
    @Resource
    private AppConfig appConfig;
    public FileUploadDto uploadFile2Loacl(MultipartFile file, String folder, FileUploadTypeEnum typeEnum){
        try{
            FileUploadDto fileUploadDto = new FileUploadDto();
            String filename = file.getOriginalFilename();
            String fileSuffix = StringUtil.getsuffix(filename);
            if(filename.length() > 200){//名字过长，进行转化
                filename = StringUtil.getFileName(filename).substring(0, 190) + fileSuffix;
            }
            if(!ArrayUtils.contains(typeEnum.getSuffixArray(), fileSuffix)){
                throw new ChillRainBBSException("格式不正确");
            }
            String month = DateUtil.format(new Date(), DateFormatEnum.YYYYMM.getFormat());
            String baseFolder = appConfig.getProgramFolder() + Constant.FILE_FOLDER_FILE;
            File targetFileFolder = new File(baseFolder + folder + month + "/");
            File targetFile = new File(targetFileFolder.getPath() + "/" + filename);
            String loaclPath = month + "/" + filename;
            if(typeEnum == FileUploadTypeEnum.AVATAR){//上传头像
                targetFileFolder = new File(baseFolder + Constant.FILE_FOLDER_AVATAR);
                targetFile = new File(targetFileFolder.getPath() + "/" + folder + Constant.AVATAR_SUFFIX);
                loaclPath = folder + Constant.AVATAR_SUFFIX;
            }
            if(!targetFileFolder.exists()){//文件夹不存在 创建文件夹
                targetFileFolder.mkdirs();
            }
            file.transferTo(targetFile);//存储
            if(typeEnum == FileUploadTypeEnum.COMMENT_IMAGE){//评论图片 需要压缩
                String thumbnailName = targetFile.getName().replace(".", "_.");
                File thumbFile = new File(targetFile.getParent() + "/" + thumbnailName);
                Boolean thumbnail = imageUtil.createThumbnail(targetFile, 200, 200, thumbFile);
                if(thumbnail){
                    FileUtils.copyFile(targetFile, thumbFile);
                }
            } else if (typeEnum == FileUploadTypeEnum.ARTICLE_COVER || typeEnum == FileUploadTypeEnum.AVATAR) {
                Boolean thumbnail = imageUtil.createThumbnail(targetFile, 200, 200, targetFile);
            }
            fileUploadDto.setLocalPath(loaclPath);
            fileUploadDto.setOriginalFileName(filename);
            return fileUploadDto;
        }catch (Exception e){
            throw new ChillRainBBSException("上传失败");
        }
    }
}
