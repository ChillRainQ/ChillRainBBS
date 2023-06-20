package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 用户积分操作
 */
public enum UserIntegralOperTypeEnum {
    REGISTER(1, "账号注册"),
    USER_DOWNLOAD_ATTACHMENT(2, "下载附件"),
    DOWNLOAD_ATTACHMENT(3, "附件被下载"),
    POST_COMMENT(4, "评论发布"),
    POST_ARTICLE(5, "文章发布"),
    ADMIN(6, "管理员操作"),
    DEL_ARTICLE(7, "文章被删除"),
    DEL_COMMENT(8, "评论被删除");

    private Integer operType;
    private String desc;

    public Integer getOperType() {
        return operType;
    }

    public String getDesc() {
        return desc;
    }

    UserIntegralOperTypeEnum(Integer operType, String desc) {
        this.operType = operType;
        this.desc = desc;
    }
    public UserIntegralOperTypeEnum getByType(Integer operType){
        for(UserIntegralOperTypeEnum item : UserIntegralOperTypeEnum.values()){
            if(item.getOperType().equals(operType)){
                return item;
            }
        }
        return null;
    }
}
