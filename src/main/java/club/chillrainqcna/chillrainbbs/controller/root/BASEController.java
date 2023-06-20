package club.chillrainqcna.chillrainbbs.controller.root;

import club.chillrainqcna.chillrainbbs.entity.bean.Response;
import club.chillrainqcna.chillrainbbs.entity.enums.ResponseCodeEnum;

/**
 * @author ChillRain 2023 04 15
 */
public class BASEController {//根Controller，不用于处理请求 只用于向前端发送信息
    private final String SUCCESS = "SUCCESS";
    private final String ERROR = "ERROR";

    /**
     * 请求成功
     * @param data 要发送的数据
     * @return
     * @param <T> 数据的类型
     */
    protected<T> Response getSuccessResponse(T data){
        Response<T> response = new Response<>();
        response.setCode(ResponseCodeEnum.CODE_200.getCode());
        response.setInfo(ResponseCodeEnum.CODE_200.getMess());
        response.setStatus(SUCCESS);
        response.setData(data);
        return response;
    }
}
