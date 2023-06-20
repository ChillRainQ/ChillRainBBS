package club.chillrainqcna.chillrainbbs.utils;



import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class IPUtil {
    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    public static String getIAddress(String ip){
        String url = "http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=";
        String site = null;
        String responseJson = HttpUtil.HTTPGet(url + ip);
        if(responseJson == null){
            return "未知位置";
        }
        Map<String, String> addressInfo = JsonUtil.json2object(responseJson, Map.class);
        site = addressInfo.get("pro");
        if(site == "") site = "未知位置";
        return site;
    }

}
