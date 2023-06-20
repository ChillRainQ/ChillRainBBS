package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 消息状态
 * @author ChillRain 2023 04 17
 */
public enum MessageStatusEnum {
    NOT_READ(1, "未读"),
    READ(2, "已读");
    private Integer status;
    private String desc;

    public Integer getStatus() {
        return status;
    }


    public String getDesc() {
        return desc;
    }


    MessageStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * 按照status返回对应的枚举
     * @param status
     * @return
     */
    public MessageStatusEnum getByStatus(Integer status){
        for(MessageStatusEnum item : MessageStatusEnum.values()){
            if(item.getStatus().equals(status)){
                return item;
            }
        }
        return null;
    }
}
