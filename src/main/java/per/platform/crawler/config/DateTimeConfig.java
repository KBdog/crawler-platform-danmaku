package per.platform.crawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author kbdog
 * @package per.platform.crawler.config
 * @description
 * @date 2021/10/26 10:38
 */
@Configuration
public class DateTimeConfig {
    @Value("${crawler.timeFormat}")
    private String timeRegex;
    @Bean
    public SimpleDateFormat dateFormat(){
        return new SimpleDateFormat(timeRegex);
    }
}
