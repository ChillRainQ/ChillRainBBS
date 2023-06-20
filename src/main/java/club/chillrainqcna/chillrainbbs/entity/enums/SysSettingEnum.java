package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 系统设置
 */
public enum SysSettingEnum {
    AUDIT("audit", "club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4AuditDto","audit", "审核设置"),
    COMMENT("comment", "club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4CommentDto","comment", "评论设置"),
    EMAIL("email", "club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4EmailDto", "email", "邮件设置"),
    LIKE("like", "club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4LikeDto", "like", "点赞设置"),
    POST("post", "club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4PostDto", "post", "帖子设置"),
    REGISTER("register", "club.chillrainqcna.chillrainbbs.entity.systemSetting.SystemSetting4Register", "register", "注册设置");
    private String code;
    private String className;
    private String propName;
    private String desc;

    SysSettingEnum(String code, String className, String propName, String desc) {
        this.code = code;
        this.className = className;
        this.propName = propName;
        this.desc = desc;
    }

    /**
     * 用于获取对应的枚举
     * @param code
     * @return
     */
    public static SysSettingEnum getByCode(String code){
        for (SysSettingEnum item : SysSettingEnum.values()){
            if(item.getCode().equals(code)) return item;
        }
        return null;
    }
    public String getPropName() {
        return propName;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    public String getClassName() {
        return className;
    }
}
