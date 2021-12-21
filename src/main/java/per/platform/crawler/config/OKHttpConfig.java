package per.platform.crawler.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author kbdog
 * @package per.platform.crawler.config
 * @description okhttp客户端配置
 * @date 2021/8/10 17:51
 */
@Configuration
public class OKHttpConfig {
    @Bean(name = "proxy")
    public Proxy proxy(){
        Proxy proxy=new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("127.0.0.1",1080));
        return proxy;
    }

    @Bean(name = "client")
    public OkHttpClient okHttpClient(@Autowired Proxy proxy){
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .proxy(proxy)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    //不使用代理的okhttpclient
    @Bean(name = "noProxyClient")
    public OkHttpClient okHttpClient(){
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

}
