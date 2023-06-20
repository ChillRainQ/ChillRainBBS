package club.chillrainqcna.chillrainbbs.entity.enums;

/**
 * 待用 用途未知
 */
public enum VerifyEnum {
    NO("", "不校验"),
    IP("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]).(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-7])){3}", "IP地址"),
    POSITIVE_INTEGER("^[0-9] *[1-9][0-9]*$", "正整数"),
    NUMBER_LETTER_UNDER_LINE("^\\w+$", "由数字，26个字母或下划线组成的字符串"),
//    PHONE("")
    ;
    private String regex;
    private String desc;

    VerifyEnum(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

    public String getRegex() {
        return regex;
    }

    public String getDesc() {
        return desc;
    }
}
