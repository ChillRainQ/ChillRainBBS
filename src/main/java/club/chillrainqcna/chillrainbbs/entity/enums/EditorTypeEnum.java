package club.chillrainqcna.chillrainbbs.entity.enums;

public enum EditorTypeEnum {
    RICH_EDITOR("富文本编辑器", 0),
    MARKDOWN_EDITOR("markdown编辑器", 1)
    ;
    private String desc;
    private Integer type;
    public static EditorTypeEnum getEditorTypeByType(Integer type){
        for(EditorTypeEnum item : EditorTypeEnum.values()){
            if(item.getType() == type){
                return item;
            }
        }
        return null;
    }
    EditorTypeEnum(String desc, Integer type) {
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
