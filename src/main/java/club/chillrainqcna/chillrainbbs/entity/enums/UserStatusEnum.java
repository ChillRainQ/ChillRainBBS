package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 *用户状态
 */
public enum UserStatusEnum {
    BAN(-1, "禁用"),
    NORMAL(1, "正常");
    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static UserStatusEnum getByStatus(Integer status) {
        for(UserStatusEnum item : UserStatusEnum.values()){
            if(item.getStatus().equals(status)){
                return item;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
