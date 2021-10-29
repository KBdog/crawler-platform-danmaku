package per.platform.crawler.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

/**
 * @author lipeiyu
 * @package com.selenium.test.utils
 * @description 谷歌驱动工具类
 * @date 2021/10/22 14:48
 */
public class DriverTools {
    /**
     * 解析当前驱动所访问的页面
     * @param waitSecond
     * @param driver
     * @return
     */
    public static Document parseCurrentWebPage(long waitSecond,WebDriver driver){
        driver.manage().timeouts().implicitlyWait(waitSecond, TimeUnit.SECONDS);
        WebElement htmlElement = driver.findElement(By.xpath("/html"));
        String pageHtml = htmlElement.getAttribute("outerHTML");
        return Jsoup.parse(pageHtml);
    }
}
