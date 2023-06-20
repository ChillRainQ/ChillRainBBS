package club.chillrainqcna.chillrainbbs.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @author ChillRain 2023 04 16
 */
public class JsonUtil {
    public static String object2Json(Object obj){
        return JSON.toJSONString(obj);
    }

    public static <T> T json2object(String json, Class<T> clasz){
        return JSON.parseObject(json, clasz);
    }

    public static <T> List<T> convertJson2Obj(String content, Class<T> clazz) {
        return JSONArray.parseArray(content, clazz);
    }
}
