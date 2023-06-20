package club.chillrainqcna.chillrainbbs.utils;

import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ChillRain 2023 04 17
 */
public class HttpUtil {
    private static final Integer TIME_OUT_SECONDS = 5;
    private static OkHttpClient.Builder getClientBuilder(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS).readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS);
        return builder;
    }
    private static Request.Builder getRequestBuilder(Map<String, String> head){
        Request.Builder builder = new Request.Builder();
        if(head != null){
            for(Map.Entry<String, String> entry: head.entrySet()){
                String key = entry.getKey();
                String value = null;
                if(entry.getValue() == null){
                    value = "";
                }else{
                    value = entry.getValue();
                }
                builder.addHeader(key, value);
            }
        }
        return builder;
    }
//    private static FormBody.Builder getFormBodyBuilder(Map<String, String> params){
//
//    }
    /**
     * OKHTTP3发送GET请求步骤
     * 1.创建client构建器
     * 2.创建request构建器 并通过构建器写入url
     * 3.通过client构建器构建client
     * 4.使用client.newCall(request)发送请求并获得response
     */

    /**
     *
     * @param url
     * @return
     */
    public static String HTTPGet(String url){
        ResponseBody responseBody = null;
        try{
            OkHttpClient.Builder clientBuilder = getClientBuilder();
            Request.Builder requestBuilder = getRequestBuilder(null);
            Request request = requestBuilder.url(url).build();
            OkHttpClient client = clientBuilder.build();
            Response response = client.newCall(request).execute();
            responseBody = response.body();
            return responseBody.string();
        } catch (IOException e) {
            throw new ChillRainBBSException("HTTP请求异常");
        }finally {
            if(responseBody != null){
                responseBody.close();
            }
        }
    }
}
