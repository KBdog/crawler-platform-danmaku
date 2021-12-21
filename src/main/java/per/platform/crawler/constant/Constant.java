package per.platform.crawler.constant;

import okhttp3.Headers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kbdog
 * @package per.platform.crawler.constant
 * @description 默认常量
 * @date 2021/8/10 23:35
 */
public class Constant {
    //默认请求头
    public static Headers.Builder DEFAULT_HEADERS_BUILDER;
    private static final String DEFAULT_HEADER_USERAGENT ="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36";
    private static final String DEFAULT_HEADER_ACCEPT ="text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
    public static final Map<String,String> DEFAULT_HEADERS;
    //豆瓣请求头
    public static Headers.Builder DOUBAN_HEADERS_BUILDER;
    private static final String DOUBAN_HEADER_ACCEPT="*/*";
    private static final String DOUBAN_HEADER_USERAGENT="api-client/1 com.douban.frodo/7.13.0(223) Android/22 product/MLA-AL10 vendor/HUAWEI model/TAS-AN00  rom/android  network/wifi  udid/eb999274af1c81fa28168a94cefb413e5c1cba70  platform/AndroidPad nd/1";
    public static final Map<String,String> DOUBAN_HEADERS;
    static {
        /**
         * 默认请求头map
         * 默认请求头builder
         */
        DEFAULT_HEADERS =new HashMap<>();
        DEFAULT_HEADERS.put("Accept", DEFAULT_HEADER_ACCEPT);
        DEFAULT_HEADERS.put("User-Agent", DEFAULT_HEADER_USERAGENT);
        DEFAULT_HEADERS_BUILDER =new Headers.Builder();
        for(Map.Entry<String,String>tmp: DEFAULT_HEADERS.entrySet()){
            DEFAULT_HEADERS_BUILDER.add(tmp.getKey(),tmp.getValue());
        }
        /**
         * 豆瓣请求头map
         * 豆瓣请求头builder
         */
        DOUBAN_HEADERS=new HashMap<>();
        DOUBAN_HEADERS.put("Accept", DOUBAN_HEADER_ACCEPT);
        DOUBAN_HEADERS.put("User-Agent", DOUBAN_HEADER_USERAGENT);
        DOUBAN_HEADERS_BUILDER=new Headers.Builder();
        for(Map.Entry<String,String>tmp: DOUBAN_HEADERS.entrySet()){
            DOUBAN_HEADERS_BUILDER.add(tmp.getKey(),tmp.getValue());
        }
    }
}
