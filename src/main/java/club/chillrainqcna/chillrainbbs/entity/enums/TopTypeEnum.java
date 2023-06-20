package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 置顶状态
 * @author ChillRain 2023 04 25
 */
public enum TopTypeEnum {
    NO_TOP(0, "未置顶"),
    TOP(1, "已置顶")
    ;
    private Integer topType;
    private String desc;
    public static TopTypeEnum getTopTypeEnumByType(Integer topType){
        for(TopTypeEnum item : TopTypeEnum.values()){
            if(item.getTopType() == topType){
                return item;
            }
        }
        return null;
    }

    TopTypeEnum(Integer topType, String desc) {
        this.topType = topType;
        this.desc = desc;
    }

    public Integer getTopType() {
        return topType;
    }

    public String getDesc() {
        return desc;
    }
}
