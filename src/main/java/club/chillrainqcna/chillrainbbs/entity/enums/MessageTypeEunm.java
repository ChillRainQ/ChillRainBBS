package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 消息类型
 * @author ChillRain 2023 04 17
 */
public enum MessageTypeEunm {
    SYS(0, "sys", "系统消息"),
    COMMENT(1, "reply", "回复我的"),
    ARTICLE_LIKE(2, "likePost", "赞了我的文章"),
    COMMENT_LIKE(3, "likeComment", "赞了我的评论"),
    DOWNLOAD_ATTACHMENT(4, "downloadAttachment", "下载了附件");

    private Integer type;
    private String code;
    private String desc;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    MessageTypeEunm(Integer type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }
    public static MessageTypeEunm getByType(Integer type){
        for(MessageTypeEunm item : MessageTypeEunm.values()){
            if(item.getType() == type){
                return item;
            }
        }
        return null;
    }
    public static MessageTypeEunm getByCode(String code){
        for(MessageTypeEunm item : MessageTypeEunm.values()){
            if(item.getType() == Integer.parseInt(code)){
                return item;
            }
        }
        return null;
    }
}
