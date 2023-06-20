package club.chillrainqcna.chillrainbbs.entity.enums;

public enum CommentStatusEnum {
    AUDIT("已审核", 1),
    NOT_AUDIT("未审核", 0),
    DEL("已删除", -1);
    private String desc;
    private Integer status;

    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }

    CommentStatusEnum(String desc, Integer status) {
        this.desc = desc;
        this.status = status;
    }
}
