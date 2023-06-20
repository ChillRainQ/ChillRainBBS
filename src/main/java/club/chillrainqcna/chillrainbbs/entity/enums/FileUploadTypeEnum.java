package club.chillrainqcna.chillrainbbs.entity.enums;

import club.chillrainqcna.chillrainbbs.entity.Constant;

public enum FileUploadTypeEnum {
    ARTICLE_COVER("文章封面", Constant.IMAGE_SUFFIX),
    ARTICLE_ATTACHMENT("文章附件", new String[]{".zip", ".ZIP", ".rar", ".RAR"}),
    COMMENT_IMAGE("评论图片", Constant.IMAGE_SUFFIX),
    AVATAR("头像", Constant.IMAGE_SUFFIX)
    ;
    private String desc;
    private String[] suffixArray;

    FileUploadTypeEnum(String desc, String[] suffixArray) {
        this.desc = desc;
        this.suffixArray = suffixArray;
    }

    public String getDesc() {
        return desc;
    }

    public String[] getSuffixArray() {
        return suffixArray;
    }
}
