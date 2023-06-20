package club.chillrainqcna.chillrainbbs.entity.enums;

public enum ArticleAttachmentTypeEnum {
    NO(0, "无附件"),
    YES(1, "有附件")
    ;
    private Integer type;
    private String desc;

    ArticleAttachmentTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
