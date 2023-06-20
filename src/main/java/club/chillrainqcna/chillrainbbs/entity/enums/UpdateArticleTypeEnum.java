package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 文章统计数据
 * @author ChillRain 2023 04 21
 */
public enum UpdateArticleTypeEnum {
    READ_COUNT("阅读量", 0),
    GOOD_COUNT("点赞量", 1),
    COMMENT_COUNT("评论量", 2)
    ;
    private String desc;
    private Integer type;
    public static UpdateArticleTypeEnum getInstanceByType(Integer type){
        for(UpdateArticleTypeEnum item : UpdateArticleTypeEnum.values()){
            if(item.getType().equals(type)){
                return item;
            }
        }
        return null;
    }

    UpdateArticleTypeEnum(String desc, Integer type) {
        this.desc = desc;
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getType() {
        return type;
    }
}
