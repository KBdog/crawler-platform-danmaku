package per.platform.crawler.config;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lipeiyu
 * @package com.selenium.test.config
 * @description
 * @date 2021/10/26 9:52
 */
@Configuration
@Slf4j
public class ChromeDriverConfig {
    @Value("${local.chrome.chromeDriverPath}")
    private String chromeDriverPath;
    @Value("${local.proxy.host}")
    private String localProxyHost;
    @Value("${local.proxy.port}")
    private String localProxyPort;
    @Value("${local.proxy.using}")
    private boolean isUsingProxy;

    @Bean
    public WebDriver defaultWebDriver(){
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        String proxyString=localProxyHost+":"+localProxyPort;
        ChromeOptions options=new ChromeOptions();
        List<String> arguments=new ArrayList<>();
        if(isUsingProxy){
            log.info("使用代理:"+proxyString);
            arguments.add("--proxy-server=http://"+proxyString);
        }else {
            log.info("不使用代理");
        }
        arguments.add("--headless");
        options.addArguments(arguments);
        return new ChromeDriver(options);
    }

}
