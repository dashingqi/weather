package weather.dashingqi.com.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 获取服务器端数据的 工具类
 * Created by zhangqi on 2017/3/27.
 */

public class HttpUtil {
    /**
     * 发送求 返回服务器端数据的方法
     * @param address 服务器地址
     * @param callback 回调参数
     */
    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        //获取到 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient();
        //获取到带有地址的 request对象
        Request request = new Request.Builder().url(address).build();
        //发送请求返回服务器端的数据
        client.newCall(request).enqueue(callback);

    }
}
