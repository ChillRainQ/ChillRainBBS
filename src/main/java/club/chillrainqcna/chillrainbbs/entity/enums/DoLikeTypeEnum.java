package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 点赞类型
 */
public enum DoLikeTypeEnum {
    ARTICLE_LIKE(0, "文章点赞"),
    COMMENT_LIKE(1, "评论点赞")
    ;
    private Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    DoLikeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
