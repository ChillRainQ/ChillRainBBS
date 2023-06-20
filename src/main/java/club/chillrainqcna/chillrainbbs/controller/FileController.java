package club.chillrainqcna.chillrainbbs.controller;

import club.chillrainqcna.chillrainbbs.config.WebConfig;
import club.chillrainqcna.chillrainbbs.controller.root.BASEController;
import club.chillrainqcna.chillrainbbs.entity.Constant;
import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.utils.NotNullUtil;
import club.chillrainqcna.chillrainbbs.utils.StringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于上传图片和后端渲染图片
 * @author ChillRain 2023 04 27
 */
@RestController
@RequestMapping("/file")
public class FileController extends BASEController {
    @Resource
    private WebConfig webConfig;
    @RequestMapping("/test")
    public Response test(){
        return getSuccessResponse("test");
    }

    /**
     * 已测试
     * @param file
     * @return
     */
    @RequestMapping("/uploadImage")
    public Response uploadImage(MultipartFile file){
        if(file == null){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        String fileName = file.getOriginalFilename();//获取文件名
        String suffixName = StringUtil.getsuffix(fileName);
        if(!ArrayUtils.contains(Constant.IMAGE_SUFFIX, suffixName)){
            throw new ChillRainBBSException(ResponseCodeEnum.CODE_600);
        }
        String path = copyFile(file);
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("fileName", path);
        return getSuccessResponse(fileMap);
    }
    private String copyFile(MultipartFile file){
        try{
            String fileName = file.getOriginalFilename();
            String suffixName = StringUtil.getsuffix(fileName);
            String fileRealName = StringUtil.getRandomString(Constant.FILE_NAME_LENGTH) + suffixName;
            String folderPath = webConfig.getProjectFolder() + Constant.FILE_FOLDER_TEMP;
            File folder = new File(folderPath);
            if(!folder.exists()){
                folder.mkdirs();
            }
            File uploadFile = new File(folderPath + fileRealName);

            file.transferTo(uploadFile);//真正复制的方法
            return Constant.FILE_FOLDER_TEMP + fileRealName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *已测试
     * @param response
     * @param imageFolder
     * @param imageName
     * @return
     */
    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    public Response getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName){
        readImage(response,imageFolder,imageName);
        return null;
    }
    private void readImage(HttpServletResponse response, String imageFolder, String imageName){
        ServletOutputStream sos = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try{
            if(NotNullUtil.isEmpty(imageFolder)){
                return;
            }
            String filePath = webConfig.getProjectFolder() + imageFolder + "/" + imageName;
            String imageSuffix = StringUtil.getsuffix(imageName);
            File file = new File(filePath);
            if(!file.exists()){
                return;
            }
            imageSuffix = imageSuffix.replace(".", "");
            response.setHeader("Cache-Control", "max-age=2592000");
            response.setContentType("image/" + imageSuffix);
//            response.setContentType("application/x-msdownload; charset=utf-8");
            fis = new FileInputStream(file);
            sos = response.getOutputStream();
//            baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int ch = 0;
            while ((ch = fis.read(bytes)) != -1){
//                baos.write(ch);
                sos.write(bytes, 0, ch);
            }
//            byte[] bytes = new byte[1024];
//            int len = 0;
//            while((len = bfis.read(bytes)) != -1){
//                baos.write(bytes, 0, len);
//            }
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
//            baos.write(fis.read());
//            sos.write(baos.toByteArray());
            sos.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                    if(sos != null) sos.close();
                    if(baos != null) baos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
