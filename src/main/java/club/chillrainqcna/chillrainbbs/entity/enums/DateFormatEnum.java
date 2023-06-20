package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * @author ChillRain 2023 05 01
 */
public enum DateFormatEnum {
    YYYYMM("yyyyMM")
    ;
    private String format;

    public String getFormat() {
        return format;
    }

    DateFormatEnum(String format) {
        this.format = format;
    }
}
