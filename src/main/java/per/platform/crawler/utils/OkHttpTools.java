package per.platform.crawler.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kbdog
 * @package per.platform.crawler.utils
 * @description okhttp工具类
 * @date 2021/8/11 11:57
 */
public class OkHttpTools {
    /**
     * 构建好request后的get请求
     * @param request
     * @param client
     * @return
     * @throws IOException
     */
    public static Response getResponse(Request request, OkHttpClient client) throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * 手动构建request的get请求
     * @param url
     * @param headers
     * @param client
     * @return
     * @throws IOException
     */
    public static Response getResponse(String url, Headers.Builder headers, OkHttpClient client) throws IOException {
        Request request=new Request.Builder().headers(headers.build()).url(url).build();
        return client.newCall(request).execute();
    }


    /**
     * 手动构建request的post请求(json格式body)
     * @param url
     * @param headers
     * @param jsonString
     * @param client
     * @return
     * @throws IOException
     */
    public static Response postResponse(String url, String jsonString, Headers.Builder headers, OkHttpClient client) throws IOException {
        RequestBody requestBody= RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonString);
        Request request=new Request.Builder().headers(headers.build()).url(url).post(requestBody).build();
        return client.newCall(request).execute();
    }

    /**
     * 传递表单数据post请求
     * @param url
     * @param formMap
     * @param headers
     * @param client
     * @return
     * @throws IOException
     */
    public static Response postResponseFromForm(String url, Map<String,String>formMap, Headers.Builder headers, OkHttpClient client) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        for(Map.Entry<String,String>map:formMap.entrySet()){
            builder.add(map.getKey(),map.getValue());
        }
        RequestBody requestBody=builder.build();
        Request request=new Request.Builder().headers(headers.build()).url(url).post(requestBody).build();
        return client.newCall(request).execute();
    }




    /**
     * 手动拼接url
     * @param url
     * @param paramMap
     * @return
     */
    public static String getUrl(String url,Map<String,String>paramMap){
        if(paramMap!=null&&paramMap.size()>0){
            url+="?";
            for(Map.Entry<String,String>tmp:paramMap.entrySet()){
                url+=tmp.getKey()+"="+tmp.getValue()+"&";
            }
        }
        return url;
    }

    /**
     * 解析api
     * @param request
     * @param client
     * @return
     * @throws IOException
     */
    public static JSONObject parseAPI(Request request, OkHttpClient client) throws IOException {
        Response response = client.newCall(request).execute();
        String jsonString = response.body().string();
        response.body().close();
        return JSONObject.parseObject(jsonString);
    }

    /**
     * 构建请求,不带headers
     * @param url
     * @param paramMap
     * @return
     */
    public static Request buildRequest(String url, Map<String,String> paramMap){
        HttpUrl.Builder httpUrlBuilder= HttpUrl.parse(url).newBuilder();
        if(paramMap!=null&&paramMap.size()>0){
            for(Map.Entry<String,String>tmpParam:paramMap.entrySet()){
                httpUrlBuilder.addQueryParameter(tmpParam.getKey(),tmpParam.getValue());
            }
        }
        HttpUrl httpUrl = httpUrlBuilder.build();
        Request request=new Request.Builder().get().url(httpUrl).build();
        return request;
    }



    /**
     * 获取客户端:用于代理经常修改的情况
     * @param proxy
     * @return
     */
    public static OkHttpClient buildClient(Proxy proxy){
        OkHttpClient client=new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .proxy(proxy)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        return client;
    }
}
