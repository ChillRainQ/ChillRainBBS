package club.chillrainqcna.chillrainbbs.exception;

import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;

/**
 * @author ChillRain 2023 04 15
 */
public class ChillRainBBSException extends RuntimeException{
    private ResponseCodeEnum codeEnum;

    private Integer code;

    private String message;

    public ChillRainBBSException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public ChillRainBBSException(String message) {
        super(message);
        this.message = message;
    }

    public ChillRainBBSException(Throwable e) {
        super(e);
    }

    public ChillRainBBSException(ResponseCodeEnum codeEnum) {
        super(codeEnum.getMess());
        this.codeEnum = codeEnum;
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMess();
    }

    public ChillRainBBSException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ResponseCodeEnum getCodeEnum() {
        return codeEnum;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 重写fillInStackTrace 业务异常不需要堆栈信息，提高效率.
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
