package per.platform.crawler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kbdog
 * @package per.kbwebstack.crawler.api.config
 * @description 默认线程池配置
 * @date 2021/8/11 12:40
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService executorService(){
        //设置十个线程
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        return fixedThreadPool;
    }

    @Bean
    public CompletionService<String>completionService(@Autowired ExecutorService executorService){
        CompletionService<String> cs = new ExecutorCompletionService<String>(executorService);
        return cs;
    }
}
