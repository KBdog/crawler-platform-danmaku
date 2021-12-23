package per.platform.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import per.platform.crawler.constant.Constant;
import per.platform.crawler.model.CarDealer;
import per.platform.crawler.model.Comment;
import per.platform.crawler.model.News;
import per.platform.crawler.utils.DriverTools;
import per.platform.crawler.utils.ExcelTools;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import per.platform.crawler.utils.JSONTools;
import per.platform.crawler.utils.OkHttpTools;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@Slf4j
class DanmakuCrawlerApplicationTests {
    @Value("${local.chrome.chromeDriverPath}")
    private String chromeDriverPath;
    @Value("${local.proxy.host}")
    private String localProxyHost;
    @Value("${local.proxy.port}")
    private String localProxyPort;
    @Autowired
    private OkHttpClient client;

    @Autowired
    private WebDriver driver;

    //2021年12月23日09:28:28 新增目录
    private String excelOutPutDir="C:\\Users\\Lenovo\\Desktop\\work\\issue7\\part2\\";
    @Autowired
    private CompletionService<String>threadPool;

    @Test
    void contextLoads() {
    }

//    @Test
//    void testRunChromeDriver() {
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        options.addArguments("--proxy-server=http://"+proxyString);
//        WebDriver driver=new ChromeDriver(options);
//        try {
//            driver.get("https://author.baidu.com/home?from=bjh_article&app_id=1683791004794452");
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//            WebElement articlePage = driver.findElement(By.xpath("/html/body/div[2]/div/div[4]/div[1]/div[1]/div/div[1]/div[1]/div/div[4]"));
//            articlePage.click();
//            WebElement webElement_1 = driver.findElement(By.xpath("/html"));
//            String content = webElement_1.getAttribute("outerHTML");
//            Document document_1 = Jsoup.parse(content);
//            Element contentNum = document_1.getElementsByClass("info-num").first();
//            int num = Integer.parseInt(contentNum.text());
//            System.out.println("共有"+num+"篇内容");
//            int sign=1;
//            JavascriptExecutor jse = (JavascriptExecutor) driver;
//            //翻页
//            while(true){
//                System.out.println("第"+sign+"次翻页");
//                WebElement webElement_2 = driver.findElement(By.xpath("/html"));
//                content = webElement_2.getAttribute("outerHTML");
//                Document document_2 = Jsoup.parse(content);
//                Elements elements = document_2.getElementsByClass("text-title line-clamp-2");
//                System.out.println("目前已收录文章的总量:"+elements.size());
//                for (Element element : elements) {
//                    System.out.println(element.text());
//                }
//                //到底了
//                if(document_2.getElementsByClass("s-loader-container state-2").first()!=null){
//                    break;
//                }
//                jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                sign++;
//                Thread.sleep(2000);
//            }
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//            WebElement webElement_2 = driver.findElement(By.xpath("/html"));
//            content = webElement_2.getAttribute("outerHTML");
//            Document document = Jsoup.parse(content);
//            Elements elements = document.getElementsByClass("text-title line-clamp-2");
//            for (Element element : elements) {
//                System.out.println(element.text());
//            }
//        }catch (Exception e){
//            System.out.println("异常:"+e.getMessage());
//        }finally {
//            //关闭全部驱动
//            driver.quit();
//        }
//    }
//
//    @Test
//    void testGetNewsFromBaidu(){
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
////        arguments.add("--headless");
////        arguments.add("--start-maximized");
////        arguments.add("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        try {
//            //拿这个新闻10页数据
//            int flag=0;
//            while(true){
//                String url="https://www.google.co.jp/search?q=github&start="+(flag*10);
//                log.info("url:"+url);
//                driver.get(url);
//                flag++;
//                Document document = DriverTools.parseCurrentWebPage(5,driver);
//                Elements newsItem = document.getElementsByClass("g");
//                if(newsItem==null||newsItem.size()==0){
//                    log.info("=====================全部相关新闻已抓取完毕=====================");
//                    break;
//                }
//                for (Element element : newsItem) {
//                    log.info("");
//                    Element h3 = element.getElementsByTag("h3").first();
//                    if(h3!=null){
//                        log.info(h3.text());
//                    }
//                    Element a = element.getElementsByTag("a").first();
//                    log.info(a.attr("href"));
//                }
//                log.info("=====================第"+flag+"页已抓取完毕=====================");
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//            //关闭全部驱动
//            driver.quit();
//        }
//    }
//
//    @Test
//    void testGetNewsFromNetease() {
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
////        arguments.add("--headless");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        Map<String,String>resultMap=new HashMap<>();
//        FileOutputStream fos=null;
//        try {
//            Map<String,String>newsMap=new HashMap<>();
//            newsMap.put("国际","https://news.163.com/world/");
//            newsMap.put("航空","https://news.163.com/air/");
//            newsMap.put("军事","https://war.163.com/");
//            newsMap.put("国内","https://news.163.com/domestic/");
//            for (Map.Entry<String,String>tmp:newsMap.entrySet()){
//                String tagName=tmp.getKey();
//                String url=tmp.getValue();
//                log.info("========================================专栏 "+tagName+"========================================");
//                driver.get(url);
//                log.info("url:"+url);
//                Document document = DriverTools.parseCurrentWebPage(5, driver);
//                Elements newsList = document.getElementsByClass("data_row news_article clearfix ");
//                for (Element newsItem : newsList) {
//                    Element h3 = newsItem.getElementsByTag("h3").first();
//                    if(h3!=null){
//                        String link = h3.getElementsByTag("a").attr("href");
//                        resultMap.put(h3.text(),link);
//                        log.info(h3.text()+" "+link);
//                    }
//                }
//                log.info("");
//            }
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            long currentTimeStamp = System.currentTimeMillis();
//            File excelFile=new File("C:\\Users\\Lenovo\\Desktop\\export\\网易新闻_"+format.format(currentTimeStamp)+".xlsx");
//            fos=new FileOutputStream(excelFile);
//            String titles[]=new String[]{"标题","链接"};
//            boolean flag = ExcelTools.exportExcelForNews(resultMap, titles, fos);
//            if(flag){
//                log.info("导出报表成功! "+excelFile.getAbsolutePath());
//            }else {
//                log.info("导出报表失败! "+excelFile.getAbsolutePath());
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//            if(fos!=null){
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            driver.quit();
//        }
//    }
//
//    @Test
//    void testGetNewsFromNeteaseV2(){
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
////        arguments.add("--headless");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        List<News>resultList=new ArrayList<>();
//        FileOutputStream fos=null;
//        try {
//            Map<String,String>newsMap=new HashMap<>();
//            newsMap.put("国际","https://news.163.com/world/");
//            newsMap.put("航空","https://news.163.com/air/");
//            newsMap.put("军事","https://war.163.com/");
//            newsMap.put("国内","https://news.163.com/domestic/");
//            for (Map.Entry<String,String>tmp:newsMap.entrySet()){
//                String tagName=tmp.getKey();
//                String url=tmp.getValue();
//                log.info("========================================专栏 "+tagName+"========================================");
//                driver.get(url);
//                log.info("url:"+url);
//                Document document = DriverTools.parseCurrentWebPage(5, driver);
//                Elements newsList = document.getElementsByClass("data_row news_article clearfix ");
//                for (Element newsItem : newsList) {
//                    Element h3 = newsItem.getElementsByTag("h3").first();
//                    if(h3!=null){
//                        String publishTime="";
//                        String title=h3.text();
//                        String link = h3.getElementsByTag("a").attr("href");
//                        try {
//                            publishTime=newsItem.getElementsByClass("time").first().text();
//                        }catch (NullPointerException e){
//                            log.info("新闻标题:"+title+" 没有发布时间");
//                        }
//                        News news=new News();
//                        news.setNewsTitle(title);
//                        news.setPublishDate(publishTime);
//                        news.setNewsLink(link);
//                        resultList.add(news);
//                        if(publishTime.equals("")){
//                            log.info(h3.text()+" "+link);
//                        }else {
//                            log.info(publishTime+" "+h3.text()+" "+link);
//                        }
//
//                    }
//                }
//                log.info("");
//            }
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            long currentTimeStamp = System.currentTimeMillis();
//            File excelFile=new File("C:\\Users\\Lenovo\\Desktop\\export\\网易新闻_"+format.format(currentTimeStamp)+".xlsx");
//            fos=new FileOutputStream(excelFile);
//            String titles[]=new String[]{"日期","标题","链接"};
//            boolean flag = ExcelTools.exportExcelForNewsV2(resultList, titles, fos);
//            if(flag){
//                log.info("导出报表成功! "+excelFile.getAbsolutePath());
//            }else {
//                log.info("导出报表失败! "+excelFile.getAbsolutePath());
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//            if(fos!=null){
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            driver.quit();
//        }
//    }
//
//    @Test
//    void testGetNewsFromTencentNews(){
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
//        arguments.add("--headless");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        List<News>resultList=new ArrayList<>();
//        FileOutputStream fos=null;
//        try {
//            Map<String,String>newsMap=new HashMap<>();
//            newsMap.put("军事","https://new.qq.com/ch/milite/");
//            newsMap.put("国际","https://new.qq.com/ch/world/");
//            for (Map.Entry<String,String>tmp:newsMap.entrySet()){
//                String tagName=tmp.getKey();
//                String url=tmp.getValue();
//                log.info("========================================专栏 "+tagName+"========================================");
//                driver.get(url);
//                log.info("url:"+url);
//                Document document = DriverTools.parseCurrentWebPage(5, driver);
//                Elements newsList = document.getElementsByClass("item cf itme-ls");
//                for (Element newsItem : newsList) {
//                    Element h3 = newsItem.getElementsByTag("h3").first();
//                    if(h3!=null){
//                        String publishTime="";
//                        String title=h3.text();
//                        String link = h3.getElementsByTag("a").attr("href");
//                        try {
//                            publishTime=newsItem.getElementsByClass("time").first().text();
//                        }catch (NullPointerException e){
//                            log.info("新闻标题:"+title+" 没有发布时间");
//                        }
//                        News news=new News();
//                        news.setNewsTitle(title);
//                        news.setPublishDate(publishTime);
//                        news.setNewsLink(link);
//                        resultList.add(news);
//                        if(publishTime.equals("")){
//                            log.info(h3.text()+" "+link);
//                        }else {
//                            log.info(publishTime+" "+h3.text()+" "+link);
//                        }
//
//                    }
//                }
//                log.info("");
//            }
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            long currentTimeStamp = System.currentTimeMillis();
//            File excelFile=new File("C:\\Users\\Lenovo\\Desktop\\export\\腾讯新闻_"+format.format(currentTimeStamp)+".xlsx");
//            fos=new FileOutputStream(excelFile);
//            String titles[]=new String[]{"日期","标题","链接"};
//            boolean flag = ExcelTools.exportExcelForNewsV2(resultList, titles, fos);
//            if(flag){
//                log.info("导出报表成功! "+excelFile.getAbsolutePath());
//            }else {
//                log.info("导出报表失败! "+excelFile.getAbsolutePath());
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//            if(fos!=null){
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            driver.quit();
//        }
//    }
//
//    @Test
//    void testGetDouyinDanmaku(){
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
//        arguments.add("--headless");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        FileOutputStream fos=null;
//        //最后结果集
//        List<Comment>resultList=new ArrayList<>();
//        try {
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            driver.get("https://live.douyin.com/361120838352");
//            long startTimeMillis = System.currentTimeMillis();
//            //十分钟
//            long minutes=5;
//            long endTimeMillis=startTimeMillis+(minutes*60*1000);
//            String startTime = format.format(startTimeMillis);
//            String endTime=format.format(endTimeMillis);
//            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
//            long currentTimeMillis=new Date().getTime();
//            //记录重复发言
//            List<Comment>repeatList=new ArrayList<>();
//            while(currentTimeMillis<endTimeMillis){
//                currentTimeMillis=new Date().getTime();
//                Document document = DriverTools.parseCurrentWebPage(15, driver);
//                Elements danmakuList = document.getElementsByClass("webcast-chatroom___item");
//                for(int i=0;i<danmakuList.size();i++){
//                    if(i==0){
//                        continue;
//                    }
//                    try {
//                        Element element = danmakuList.get(i);
//                        Element commentContent = element.getElementsByTag("div").first();
//                        String userName = commentContent.getElementsByClass("_205YX559")
//                                .first().text().replaceAll("：","");
//                        String userComment = commentContent.getElementsByClass("_2Fj-jpg0")
//                                .first().text();
//                        if(userComment.equals("来了")){
//                            continue;
//                        }
//                        Comment comment=new Comment();
//                        comment.setCommentTime(format.format(currentTimeMillis));
//                        comment.setUserName(userName);
//                        comment.setUserComment(userComment);
//                        boolean isRepeat=false;
//                        //判断是不是重复评论
//                        for (Comment tmp : repeatList) {
//                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
//                                isRepeat=true;
//                                break;
//                            }
//                        }
//                        if(isRepeat==false){
//                            log.info(format.format(currentTimeMillis)+"--"+userName+"--"+userComment);
//                            repeatList.add(comment);
//                            resultList.add(comment);
//                        }
//                    }catch (Exception e){
//                        continue;
//                    }
//                }
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            long currentTimeStamp = System.currentTimeMillis();
//            File excelFile=new File("C:\\Users\\Lenovo\\Desktop\\export\\抖音评论_"+format.format(currentTimeStamp)+".xlsx");
//            try {
//                fos=new FileOutputStream(excelFile);
//                String titles[]=new String[]{"评论时间","用户","评论内容"};
//                boolean flag=false;
//                if(resultList.size()>0){
//                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
//                }
//                if(flag){
//                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
//                }else {
//                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
//                }
//            } catch (FileNotFoundException e) {
//                log.error(e.getMessage());
//                e.printStackTrace();
//            }finally {
//                if(fos!=null){
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                driver.quit();
//            }
//        }
//    }
//
//    @Test
//    void testGetHuyaDanmaku(){
////        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
////        String proxyString=localProxyHost+":"+localProxyPort;
////        ChromeOptions options=new ChromeOptions();
////        List<String>arguments=new ArrayList<>();
////        arguments.add("--proxy-server=http://"+proxyString);
////        arguments.add("--headless");
////        options.addArguments(arguments);
////        WebDriver driver=new ChromeDriver(options);
//        FileOutputStream fos=null;
//        //最后结果集
//        List<Comment>resultList=new ArrayList<>();
//        try {
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            driver.get("https://www.huya.com/257085");
//            long startTimeMillis = System.currentTimeMillis();
//            //十分钟
//            long minutes=2;
//            long endTimeMillis=startTimeMillis+(minutes*60*1000);
//            String startTime = format.format(startTimeMillis);
//            String endTime=format.format(endTimeMillis);
//            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
//            long currentTimeMillis=new Date().getTime();
//            //记录重复发言
//            List<Comment>repeatList=new ArrayList<>();
//            while(currentTimeMillis<endTimeMillis){
//                currentTimeMillis=new Date().getTime();
//                Document document = DriverTools.parseCurrentWebPage(15, driver);
//                Elements danmakuList = document.getElementsByClass("J_msg");
//                for(int i=0;i<danmakuList.size();i++){
//                    if(i==0){
//                        continue;
//                    }
//                    try {
//                        Element element = danmakuList.get(i);
//                        Element commentContent = element.getElementsByTag("div").first();
//                        String userName = commentContent.getElementsByClass("name J_userMenu")
//                                .first().text();
//                        String userComment = commentContent.getElementsByClass("msg")
//                                .first().text();
//                        if(userComment.equals("驾临直播间")){
//                            continue;
//                        }
//                        Comment comment=new Comment();
//                        comment.setCommentTime(format.format(currentTimeMillis));
//                        comment.setUserName(userName);
//                        comment.setUserComment(userComment);
//                        boolean isRepeat=false;
//                        //判断是不是重复评论
//                        for (Comment tmp : repeatList) {
//                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
//                                isRepeat=true;
//                                break;
//                            }
//                        }
//                        if(isRepeat==false){
//                            log.info(format.format(currentTimeMillis)+"--"+userName+"--"+userComment);
//                            repeatList.add(comment);
//                            resultList.add(comment);
//                        }
//                    }catch (Exception e){
//                        continue;
//                    }
//                }
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            long currentTimeStamp = System.currentTimeMillis();
//            File excelFile=new File("C:\\Users\\Lenovo\\Desktop\\export\\虎牙弹幕_"+format.format(currentTimeStamp)+".xlsx");
//            try {
//                fos=new FileOutputStream(excelFile);
//                String titles[]=new String[]{"评论时间","用户","评论内容"};
//                boolean flag=false;
//                if(resultList.size()>0){
//                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
//                }
//                if(flag){
//                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
//                }else {
//                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
//                }
//            } catch (FileNotFoundException e) {
//                log.error(e.getMessage());
//                e.printStackTrace();
//            }finally {
//                if(fos!=null){
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                driver.quit();
//            }
//        }
//    }
//
//    @Test
//    void testGetBilibiliDanmaku(){
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
//        arguments.add("--headless");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        FileOutputStream fos=null;
//        //最后结果集
//        List<Comment>resultList=new ArrayList<>();
//        try {
//
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            driver.get("https://live.bilibili.com/22333522");
//            long startTimeMillis = System.currentTimeMillis();
//            //十分钟
//            long minutes=30;
//            long endTimeMillis=startTimeMillis+(minutes*60*1000);
//            String startTime = format.format(startTimeMillis);
//            String endTime=format.format(endTimeMillis);
//            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
//            long currentTimeMillis=new Date().getTime();
//            //记录重复发言
//            List<Comment>repeatList=new ArrayList<>();
//            while(currentTimeMillis<endTimeMillis){
//                currentTimeMillis=new Date().getTime();
//                Document document = DriverTools.parseCurrentWebPage(15, driver);
//                Elements danmakuList = document.getElementsByClass("chat-item danmaku-item ");
//                Elements tmpDanmakuList = document.getElementsByClass("chat-item danmaku-item chat-colorful-bubble");
//                danmakuList.addAll(tmpDanmakuList);
//                for(int i=0;i<danmakuList.size();i++){
//                    try {
//                        Element element = danmakuList.get(i);
//                        String userName = element.getElementsByClass("user-name v-middle pointer open-menu")
//                                .first().text().replaceAll(":","").trim();
//                        String userComment = element.getElementsByClass("danmaku-content v-middle pointer ts-dot-2 open-menu")
//                                .first().text();
//                        Comment comment=new Comment();
//                        comment.setCommentTime(format.format(currentTimeMillis));
//                        comment.setUserName(userName);
//                        comment.setUserComment(userComment);
//                        boolean isRepeat=false;
//                        //判断是不是重复评论
//                        for (Comment tmp : repeatList) {
//                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
//                                isRepeat=true;
//                                break;
//                            }
//                        }
//                        if(isRepeat==false){
//                            log.info(format.format(currentTimeMillis)+"--"+userName+"--"+userComment);
//                            repeatList.add(comment);
//                            resultList.add(comment);
//                        }
//                    }catch (Exception e){
//                        continue;
//                    }
//                }
//            }
//
//        }catch (Exception e){
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//
//            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            long currentTimeStamp = System.currentTimeMillis();
//            File excelFile=new File("C:\\Users\\Lenovo\\Desktop\\export\\B站弹幕_"+format.format(currentTimeStamp)+".xlsx");
//            try {
//                fos=new FileOutputStream(excelFile);
//                String titles[]=new String[]{"评论时间","用户","评论内容"};
//                boolean flag=false;
//                if(resultList.size()>0){
//                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
//                }
//                if(flag){
//                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
//                }else {
//                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
//                }
//            } catch (FileNotFoundException e) {
//                log.error(e.getMessage());
//                e.printStackTrace();
//            }finally {
//                if(fos!=null){
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                driver.quit();
//            }
//        }
//    }
//
//    @Test
//    void testIoPrintFile() throws IOException {
//        List<String>list=new ArrayList<>();
//        list.add("第一行");
//        list.add("第二行");
//        list.add("第三行");
//        File file=new File("C:\\Users\\Lenovo\\Desktop\\export\\test.txt");
//        FileOutputStream fos=new FileOutputStream(file);
//        PrintWriter pr=new PrintWriter(fos);
//        BufferedWriter br=new BufferedWriter(pr);
//        for (String s : list) {
//            br.write(s);
//            br.newLine();
//            br.flush();
//        }
//        br.close();
//        pr.close();
//        fos.close();
//    }
//
//    @Test
//    void testGetDouyinDanmakuV2(){
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//        String proxyString=localProxyHost+":"+localProxyPort;
//        ChromeOptions options=new ChromeOptions();
//        List<String>arguments=new ArrayList<>();
//        arguments.add("--proxy-server=http://"+proxyString);
////        arguments.add("--headless");
//        arguments.add("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36");
//        arguments.add("--disable-blink-features=AutomationControlled");
//        options.addArguments(arguments);
//        WebDriver driver=new ChromeDriver(options);
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            driver.get("https://www.douyin.com/video/6964295044824157454?previous_page=app_code_link");
//            Document document=null;
//            Element video =null;
//            //开始时间
//            long startTimeMillis = System.currentTimeMillis();
//            log.info("==================开始时间 "+format.format(startTimeMillis)+"==================");
//            //十分钟内都可以监控获取video标签
//            long minutes=10;
//            long endTimeMillis=startTimeMillis+(minutes*60*1000);
//            long currentTimeMillis=new Date().getTime();
//            while(currentTimeMillis<endTimeMillis) {
//                currentTimeMillis = new Date().getTime();
//                document = DriverTools.parseCurrentWebPage(30, driver);
//                video = document.getElementsByTag("video").first();
//                if(video!=null){
//                    break;
//                }
//            }
//            Elements source = video.getElementsByTag("source");
//            for (Element element : source) {
//                String url="https:"+element.attr("src");
//                log.info(url);
//                System.out.println(url);
//            }
//            log.info("==================结束时间 "+format.format(endTimeMillis)+"==================");
//        }catch (Exception e) {
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }finally {
//            driver.quit();
//        }
//    }

    //广汇汽车服务
    @Test
    void testGetGuanghuiqiche() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        //省份列表
        Map<Integer,String>provinceMap=new HashMap<>();
        provinceMap.put(21,"新疆");
        provinceMap.put(15,"青海");
        provinceMap.put(3,"甘肃");
        provinceMap.put(19,"四川");
        provinceMap.put(14,"宁夏");
        provinceMap.put(13,"内蒙古");
        provinceMap.put(17,"山西");
        provinceMap.put(18,"陕西");
        provinceMap.put(22,"重庆");
        provinceMap.put(6,"贵州");
        provinceMap.put(5,"广西");
        provinceMap.put(7,"河北");
        provinceMap.put(8,"河南");
        provinceMap.put(26,"湖南");
        provinceMap.put(4,"广东");
        provinceMap.put(24,"江西");
        provinceMap.put(28,"浙江");
        provinceMap.put(23,"上海");
        provinceMap.put(1,"安徽");
        provinceMap.put(11,"江苏");
        provinceMap.put(16,"山东");
        provinceMap.put(2,"北京");
        provinceMap.put(27,"天津");
        provinceMap.put(12,"辽宁");
        provinceMap.put(10,"吉林");
        provinceMap.put(9,"黑龙江");
        //url
        String url="http://www.chinagrandauto.com/src/action.php";
        Map<String,String>paramMap=new HashMap<>();
        paramMap.put("action","storeList");
        paramMap.put("lan","cn");
        for(Map.Entry<Integer,String>province:provinceMap.entrySet()){
            Integer provinceKey = province.getKey();
            String provinceName = province.getValue();
            log.info("=======================当前省份:"+province.getValue()+"=======================");
            paramMap.put("province",provinceName);
            Response response = OkHttpTools.postResponseFromForm(url, paramMap, Constant.DEFAULT_HEADERS_BUILDER, client);
            String jsonString = response.body().string();
            JSONObject cityListObj = JSONObject.parseObject(jsonString)
                    .getJSONObject("data")
                    .getJSONObject(""+provinceKey).getJSONObject("city");
            Set<String> keys = cityListObj.keySet();
            Iterator<String> iterator = keys.iterator();
            while(iterator.hasNext()){
                String cityKey = iterator.next();
                JSONObject cityObj = cityListObj.getJSONObject(cityKey);
                //城市名
                String cityName= provinceName+cityObj.getString("name");
                if(provinceName.equals(cityObj.getString("name"))){
                    cityName=provinceName;
                }
                JSONArray storeArray = cityObj.getJSONArray("store");
                for(int i=0;i<storeArray.size();i++){
                    JSONObject storeObj = storeArray.getJSONObject(i);
                    //经销商实体
                    CarDealer dealer=new CarDealer();
                    //经销商电话
                    String dealerPhone = storeObj.getString("phone1");
                    //地址
                    String dealerAddress = storeObj.getString("address");
                    //店名
                    String dealerName = storeObj.getString("store_name");
                    //品牌
                    String brandName=storeObj.getString("brand_name");
                    //赋值
                    dealer.setCityName(cityName);
                    dealer.setBrandName(brandName);
                    dealer.setDealerName(dealerName);
                    dealer.setDealerAddress(dealerAddress);
                    dealer.setDealerPhone(dealerPhone);
                    resultList.add(dealer);
                }
            }
        }
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File("C:\\Users\\Lenovo\\Desktop\\work\\issue7\\广汇汽车经销商数据.xlsx");
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }

    }

    //中升集团
    @Test
    void testGetZhongshengjituan() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        String url="https://adm.zs-group.com.cn/server/crm/uitemplate/dynQuery?access_token=senscrm&templateCode=zsauto_group_info";
        JSONObject paramObj=new JSONObject();
        paramObj.put("page",0);
        paramObj.put("pageSize",100000);
        String requestBodyString=paramObj.toJSONString();
        Response response = OkHttpTools.postResponse(url, requestBodyString, Constant.DEFAULT_HEADERS_BUILDER, client);
        String jsonString=response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("content");
        for(int i=0;i<jsonArray.size();i++){
            JSONObject dealerObj = jsonArray.getJSONObject(i);
            CarDealer dealer=new CarDealer();
            String cityName=null;
            if(dealerObj.getString("province").equals(dealerObj.getString("city"))){
                cityName=dealerObj.getString("province");
            }else {
                cityName = dealerObj.getString("province")+dealerObj.getString("city");
            }
            String brandName = dealerObj.getString("brand");
            String dealerName = dealerObj.getString("name");
            String dealerAddress = dealerObj.getString("address");
            String dealerPhone = dealerObj.getString("phone");
//            if(dealerPhone==null||dealerPhone.equals("")){
//                dealerPhone=dealerObj.getString("c_maintain_tel");
//            }
            dealer.setCityName(cityName);
            dealer.setBrandName(brandName);
            dealer.setDealerName(dealerName);
            dealer.setDealerAddress(dealerAddress);
            dealer.setDealerPhone(dealerPhone);
            resultList.add(dealer);
        }
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File("C:\\Users\\Lenovo\\Desktop\\work\\issue7\\中升集团经销商数据.xlsx");
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }


    }

    //永达汽车
    @Test
    void testGetYongdaqiche() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        //按品牌作为参数筛选
        Map<Integer,String>brandMap=new HashMap<>();
        brandMap.put(4,"保时捷");
        brandMap.put(1,"宝马");
        brandMap.put(5,"奥迪");
        brandMap.put(24,"奔驰");
        brandMap.put(6,"雷克萨斯");
        brandMap.put(2,"宾利");
        brandMap.put(3,"阿斯顿马丁");
        brandMap.put(10,"捷豹");
        brandMap.put(25,"路虎");
        brandMap.put(7,"宝诚MINI");
        brandMap.put(8,"英菲尼迪");
        brandMap.put(9,"林肯");
        brandMap.put(11,"沃尔沃");
        brandMap.put(12,"凯迪拉克");
        brandMap.put(14,"别克");
        brandMap.put(15,"荣威");
        brandMap.put(16,"雪佛兰");
        brandMap.put(17,"广汽丰田");
        brandMap.put(18,"一汽丰田");
        brandMap.put(19,"福特");
        brandMap.put(20,"广汽本田");
        brandMap.put(21,"上汽大众");
        brandMap.put(22,"斯柯达");
        brandMap.put(31,"东风本田");
        brandMap.put(23,"一汽大众");
        brandMap.put(29,"马自达");
        brandMap.put(33,"智己汽车");
        brandMap.put(30,"小鹏");
        brandMap.put(28,"威马");
        brandMap.put(32,"福特野马");
        String url="http://www.ydauto.com.cn/yd-web/carSalesNetworkController/queryPageList";
        Map<String,String>paramMap=new HashMap<>();
        paramMap.put("languageType","1");
        paramMap.put("province","");
        paramMap.put("city","");
        paramMap.put("area","");
        paramMap.put("rows","9999");
        paramMap.put("page","1");
        for(Map.Entry<Integer,String>brand:brandMap.entrySet()){
            paramMap.put("carTrademarkId",""+brand.getKey());
            String brandName=brand.getValue();
            String requestUrl = OkHttpTools.getUrl(url, paramMap);
            Response response = OkHttpTools.getResponse(requestUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String jsonString = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("records");
            for(int i=0;i<jsonArray.size();i++){
                CarDealer dealer=new CarDealer();
                JSONObject dealerObj = jsonArray.getJSONObject(i);
                String cityName=null;
                if(dealerObj.getString("province").equals(dealerObj.getString("city"))){
                    cityName=dealerObj.getString("province");
                }else {
                    cityName = dealerObj.getString("province")+dealerObj.getString("city");
                }
                String dealerName = dealerObj.getString("conpanyName");
                String dealerShortName = dealerObj.getString("conpanyShortName");
                String dealerAddress=dealerObj.getString("area")+dealerObj.getString("address");
                String dealerPhone = dealerObj.getString("phone");
                dealer.setCityName(cityName);
                dealer.setBrandName(brandName);
                if(dealerName.equals("--")||dealerName.equals("1")||dealerName.equals("··")){
                    System.out.println(dealerObj.toString());
                    dealer.setDealerName(dealerShortName);
                }else {
                    dealer.setDealerName(dealerName);
                }
                dealer.setDealerAddress(dealerAddress);
                dealer.setDealerPhone(dealerPhone);
                resultList.add(dealer);
            }
        }
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File("C:\\Users\\Lenovo\\Desktop\\work\\issue7\\永达汽车经销商数据.xlsx");
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }
    }

    //元通汽车
    @Test
    void testGetYuantongqiche() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        //按品牌作为参数筛选
        Map<Integer,String>brandMap=new HashMap<>();
        brandMap.put(63,"宝马");
        brandMap.put(25,"奥迪");
        brandMap.put(48,"雷克萨斯");
        brandMap.put(62,"林肯");
        brandMap.put(43,"奔驰");
        brandMap.put(60,"凯迪拉克");
        brandMap.put(59,"英菲尼迪");
        brandMap.put(53,"沃尔沃");
        brandMap.put(50,"进口大众");
        brandMap.put(39,"克莱斯勒/进口三菱");
        brandMap.put(28,"克莱斯勒");
        brandMap.put(8,"上汽大众");
        brandMap.put(29,"一汽大众");
        brandMap.put(23,"通用别克");
        brandMap.put(32,"雪佛兰");
        brandMap.put(51,"斯柯达");
        brandMap.put(31,"北京现代");
        brandMap.put(40,"长安福特");
        brandMap.put(22,"东风日产");
        brandMap.put(36,"一汽丰田");
        brandMap.put(44,"广汽丰田");
        brandMap.put(52,"三菱");
        brandMap.put(85,"广州本田");
        brandMap.put(47,"东风本田");
        brandMap.put(38,"一汽马自达");
        brandMap.put(55,"东风悦达起亚");
        brandMap.put(58,"东风裕隆");
        brandMap.put(27,"中华/金杯");
        brandMap.put(35,"东风风行");
        brandMap.put(33,"长安铃木");
        brandMap.put(49,"长安轿车");
        brandMap.put(24,"长安微车");
        brandMap.put(46,"长安微车/铃木");
        brandMap.put(26,"长安微车/轿车/铃木");
        brandMap.put(54,"南京依维柯");
        brandMap.put(66,"上汽大通");
        brandMap.put(34,"一汽解放");
        brandMap.put(57,"北汽新能源");
        String url="https://www.yuantong.com.cn/dot.asp";
        Map<String,String>paramMap=new HashMap<>();
        paramMap.put("area","");
        paramMap.put("city","");
        for(Map.Entry<Integer,String>brand:brandMap.entrySet()){
            //品牌名
            String brandName = brand.getValue();
            paramMap.put("brand",""+brand.getKey());
            String requestUrl = OkHttpTools.getUrl(url, paramMap);
            Response response = OkHttpTools.getResponse(requestUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            try {
                Elements liList = document.getElementsByClass("map_list").first().getElementsByTag("li");
                for (Element li : liList) {
                    CarDealer dealer=new CarDealer();
                    String liString = li.text();
                    String[] liStringArray = liString.split(" ");
                    //经销商名
                    String dealerName=li.getElementsByClass("title").first().text();;
//                    dealerName=dealerName.substring(1,dealerName.length()-1);
                    //经销商地址
                    String dealerAddress=liStringArray[1];
                    //经销商电话
                    if(liStringArray.length>2){
                        String dealerPhone=liStringArray[2].replaceAll("销售热线：","");
                        dealer.setDealerPhone(dealerPhone);
                    }
                    //城市
                    //城市正则
                    String cityName = null;
                    String cityRegex="^(.*(市|区|县))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(dealerAddress);
                    if(matcher.find()){
                        cityName=matcher.group(0);
                    }else {
                        cityName=dealerAddress.substring(0,3);
                        if(dealerAddress.endsWith("省")||dealerAddress.endsWith("区")){
                            cityName=dealerAddress.substring(0,6);
                        }
                        if(!cityName.endsWith("市")){
                            cityName=dealerName.substring(0,2);
                        }
                    }
                    dealer.setCityName(cityName);
                    dealer.setDealerName(dealerName);
                    dealer.setDealerAddress(dealerAddress);
                    dealer.setBrandName(brandName);
                    resultList.add(dealer);
                }
            }catch (NullPointerException e){
                continue;
            }
        }
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File("C:\\Users\\Lenovo\\Desktop\\work\\issue7\\元通汽车经销商数据.xlsx");
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }

    }

    //大昌行汽车
    @Test
    void testGetDachanghangqiche() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        //按品牌作为参数筛选
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("133","重汽汕德卡");
        brandMap.put("132","庆铃五十铃");
        brandMap.put("131","青岛解放");
        brandMap.put("130","陕汽重卡");
        brandMap.put("129","曼恩");
        brandMap.put("128","福田");
        brandMap.put("127","大众");
        brandMap.put("92","日产");
        brandMap.put("91","别克");
        brandMap.put("90","东风本田");
        brandMap.put("79","广汽丰田");
        brandMap.put("78","宾利");
        brandMap.put("77","奔驰");
        brandMap.put("76","雷克萨斯");
        brandMap.put("75","奥迪");
        brandMap.put("74","宝马");
        brandMap.put("73","广汽本田");
        brandMap.put("72","一汽丰田");
        String url="https://www.dchmotor.com.cn/ajax/GetJxsInfo.aspx";
        Map<String,String>paramMap=new HashMap<>();
        paramMap.put("pagenum","1000");
        paramMap.put("pageindex","1");
        paramMap.put("key","");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getValue();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("sk1","");
            jsonObject.put("sk2","");
            jsonObject.put("sk3","");
            jsonObject.put("sk4",""+brand.getKey());
            paramMap.put("searchtype",jsonObject.toJSONString());
            Response response = OkHttpTools.postResponseFromForm(url, paramMap, Constant.DEFAULT_HEADERS_BUILDER, client);
            String jsonString = response.body().string();
            JSONObject data = JSONObject.parseObject(jsonString).getJSONObject("data");
            if(data!=null){
                JSONArray dealerList = data.getJSONArray("Items");
                for(int i=0;i<dealerList.size();i++){
                    CarDealer dealer=new CarDealer();
                    JSONObject dealerObj = dealerList.getJSONObject(i);
                    String dealerAddress = dealerObj.getString("Address");
                    String dealerName = dealerObj.getString("title");
                    String dealerPhone = dealerObj.getString("SalesHotline");
                    //城市正则
                    String cityName = null;
                    String cityRegex="^(.*(市|区))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(dealerAddress);
                    if(matcher.find()){
                        cityName=matcher.group(0);
                    }else {
                        cityName = dealerName.substring(0, 2);
                    }
                    dealer.setCityName(cityName);
                    dealer.setBrandName(brandName);
                    dealer.setDealerAddress(dealerAddress);
                    dealer.setDealerName(dealerName);
                    dealer.setDealerPhone(dealerPhone);
                    resultList.add(dealer);
                }
            }
        }
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File("C:\\Users\\Lenovo\\Desktop\\work\\issue7\\大昌行汽车经销商数据.xlsx");
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }
    }


    /**
     * 2021年12月23日09:21:31 新需求
     */

    public static void  exportExcel(String excelOutPutDir,String companyName,List<CarDealer>resultList) throws IOException {
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File(excelOutPutDir+companyName);
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }
    }

    //北汽鹏龙
    @Test
    void testGetBeiqipenglong() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="北汽鹏龙经销商数据.xlsx";
        String url="https://www.rocar.net/productList/brand?brand=bz&menu_id=12";
        Response response = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
        String jsonString = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray productList = jsonObject.getJSONArray("productList");
        for(int i=0;i<productList.size();i++){
            JSONObject product = productList.getJSONObject(i);
            CarDealer carDealer=new CarDealer();
            String cityName = product.getString("recommend_location");
            String brandName = product.getString("keywords");
            String dealerName = product.getString("name");
            String dealerAddress = product.getString("enname");
            String dealerPhone = product.getString("subname");
            carDealer.setCityName(cityName);
            carDealer.setBrandName(brandName);
            carDealer.setDealerName(dealerName);
            carDealer.setDealerAddress(dealerAddress);
            carDealer.setDealerPhone(dealerPhone);
            resultList.add(carDealer);
        }
        String[]titles=new String[]{"城市","品牌","经销商","地址","电话"};
        File file=new File(excelOutPutDir+companyName);
        FileOutputStream fos=new FileOutputStream(file);
        try {
            ExcelTools.exportExcelForCarDealerV4(resultList,titles,fos);
            log.info("报表已打印完成!");
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            fos.close();
            log.info("流关闭完成!");
        }
    }

    //吉林长久实业
    @Test
    void testGetJilinchangjiushiye() throws IOException {
        //结果集
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="吉林长久实业经销商数据.xlsx";
        String indexUrl="http://www.changjiuqiche.com/index.php?m=default.brand&catid=4&cCatid=16&tCatid=32";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements liList = document.getElementsByClass("ombrCon").first().getElementsByTag("li");
        for (Element li : liList) {
            String brandName=li.text();
            String dealerUrl = "http://www.changjiuqiche.com"+li.getElementsByTag("a").first().attr("href");
            int flag=0;
            while(flag<3){
                try {
                    log.info(dealerUrl);
                    Response rsp = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                    String dealerHtml = rsp.body().string();
                    Document dealerDocument = Jsoup.parse(dealerHtml);
                    CarDealer carDealer=new CarDealer();
                    Element ambdrCon = dealerDocument.getElementsByClass("ambdrCon").first();
                    String dealerName=ambdrCon.getElementsByTag("strong").first().text().split("：")[0];
                    if(dealerName==null||dealerName.equals("")){
                        dealerName=ambdrCon.getElementsByTag("strong").get(1).text().split("：")[0];
                    }
                    String contentMessage=ambdrCon.text()
                            .replaceAll("地 址：","地址：")
                            .replaceAll("电 话（销售热线）：","电话（销售热线）：")
                            .split("地址：")[1].trim();
                    String[] contentArray = contentMessage.split(" ");
                    String address=contentArray[0];
                    String phone=null;
                    try {
                        phone=contentArray[1].split("：")[1];
                    }catch (ArrayIndexOutOfBoundsException e){
                        phone=contentArray[2].split("：")[1];
                    }
                    //城市正则
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else {
                        if(address!=null&&address!=""){
                            carDealer.setCityName(address.substring(0,2));
                        }
                    }
                    carDealer.setBrandName(brandName);
                    carDealer.setDealerName(dealerName);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerPhone(phone);

                    resultList.add(carDealer);
                    break;
                }catch (IOException e){
                    flag++;
                    log.info("第"+flag+"次重试..."+e.getMessage());
                }
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
        log.info("数据导出完成");
    }

    //广物汽贸
    @Test
    void testGetGuangwuqimao() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="广物汽贸经销商数据.xlsx";
        String indexUrl="http://www.gwqm.com/index.php?m=content&c=index&a=lists&catid=38";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Element list = document.getElementsByClass("hidden sidebrand clearfix").first();
        Elements liList = list.getElementsByTag("li");
        for(int i=0;i<liList.size()-1;i++){
            Element li = liList.get(i);
            String brandName=li.text();
            String dealerListUrl=li.getElementsByTag("a").first().attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerListUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerHtml = rsp1.body().string();
            Document dealerDocument = Jsoup.parse(dealerHtml);
            Elements dealerContentList = dealerDocument.getElementsByClass("zyd_dtewxt fl");
            for (Element dealerContent : dealerContentList) {
                CarDealer dealer=new CarDealer();
                Elements pList = dealerContent.getElementsByTag("p");
                String dealerName = pList.get(0).text().replaceAll("公司名称：", "");
                String dealerAddress = pList.get(1).text().replaceAll("公司地址：", "");
                String phone = pList.get(2).text().replaceAll("销售热线：", "");
                //城市正则
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(dealerAddress);
                if(matcher.find()){
                    dealer.setCityName(matcher.group(0));
                }else {
                    if(dealerAddress!=null&&!dealerAddress.equals("")){
                        dealer.setCityName(dealerAddress.substring(0,2));
                    }else{
                        dealer.setCityName(dealerName.substring(0,2));
                    }
                }
                dealer.setBrandName(brandName);
                dealer.setDealerAddress(dealerAddress);
                dealer.setDealerPhone(phone);
                dealer.setDealerName(dealerName);
                resultList.add(dealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
        log.info("数据导出完成");

    }


    //庞大汽贸V2
    @Test
    void testGetPangdaqimaoV2() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="庞大汽贸经销商数据.xlsx";
        String indexUrl="http://www.pdqmjt.com/UseType.aspx?classid=19";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Element sypp = document.getElementsByClass("sypp").first();
        Elements aList = sypp.getElementsByTag("a");
        for (Element a : aList) {
            String brandName=a.text();
            String brandUrl="http://www.pdqmjt.com/"+a.attr("href");
            Response rsp1 = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String brandHtml = rsp1.body().string();
            Document brandDocument = Jsoup.parse(brandHtml);
            Element span = brandDocument.getElementsByClass("col2_wz a03").first().getElementsByTag("span").first();
            Elements a1 = span.getElementsByTag("a");
            if(a1.size()>0){
                String dealerUrl="http://www.pdqmjt.com/"+a1.attr("href");
                Response rsp2 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String dealerHtml = rsp2.body().string();
                Document dealerDocument = Jsoup.parse(dealerHtml);
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerDocument.getElementsByClass("col9wz2").first().text();
                Elements pList = dealerDocument.getElementsByClass("col9a").first().getElementsByTag("p");
                String phone=pList.get(1).text().split("电话：")[1];
                String address=pList.get(2).text().split("地址：")[1];
                //城市正则
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    String cityName=matcher.group(0);
                    carDealer.setCityName(cityName);
                }else {
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }else {
                continue;
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //利泰集团
    @Test
    void testGetLitaijituan() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="利泰集团经销商数据.xlsx";
        String indexUrl="http://www.lited.com/company.php";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String indexHtml = response.body().string();
        Document indexDocument = Jsoup.parse(indexHtml);
        Elements page = indexDocument.getElementsByClass("page").first().getElementsByTag("a");
        for(int i=1;i<page.size()-1;i++){
            Element a = page.get(i);
            String dealerListUrl="http://www.lited.com/company.php"+a.attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerListUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerListHtml = rsp1.body().string();
            Document dealerListDocument = Jsoup.parse(dealerListHtml);
            Element activit = dealerListDocument.getElementById("activit");
            Elements liList = activit.getElementsByTag("li");
            for (Element li : liList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = li.getElementsByTag("p").get(0).text();
                String[]messageContent = li.getElementsByTag("p").get(2).text().split(" ");
                String brandName=messageContent[0].split("品牌:")[1];
                String dealerAddress=messageContent[1].split("地址:")[1];
                //城市正则
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(dealerAddress);
                if(matcher.find()){
                    String cityName=matcher.group(0);
                    carDealer.setCityName(cityName);
                }else {
                    carDealer.setCityName(dealerAddress.substring(0,2));
                }
                String dealerUrl="http://www.lited.com/"+li.getElementsByTag("p").get(0)
                        .getElementsByTag("a").first().attr("href");
                Response rsp2 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String dealerHtml = rsp2.body().string();
                Document dealerDocument = Jsoup.parse(dealerHtml);
                log.info(dealerUrl);
                try {
                    String phone=dealerDocument.getElementsByClass("MsoNormal")
                            .get(3).text().split("销售热线：")[1];
                    carDealer.setDealerPhone(phone);
                }catch (ArrayIndexOutOfBoundsException e){
                    String phone=dealerDocument.getElementsByClass("MsoNormal")
                            .get(4).text().split("销售热线：")[1];
                    carDealer.setDealerPhone(phone);
                }
                carDealer.setDealerAddress(dealerAddress);
                carDealer.setBrandName(brandName);
                carDealer.setDealerName(dealerName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //华星汽车
    @Test
    void testGetHuaxingqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="华星汽车经销商数据.xlsx";
        String indexUrl="https://www.sc-hstar.com/brand";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String indexHtml = response.body().string();
        Document indexDocument = Jsoup.parse(indexHtml);
        Elements dealerList = indexDocument.getElementsByClass("flex1 brand-branch flex-lc");
        List<String>brandList=new ArrayList<>();
        brandList.add("奔驰");
        brandList.add("奥迪");
        brandList.add("Jeep");
        brandList.add("现代");
        brandList.add("大众");
        for(int i=0;i<dealerList.size();i++){
            Element element = dealerList.get(i);
            String brandName=brandList.get(i);
            Element aList = element.getElementsByTag("a").first();
            //第一个站和其他都一样
            String dealerListUrl = "https://www.sc-hstar.com"+aList.attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerListUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerListHtml= rsp1.body().string();
            Document dealerListDocument = Jsoup.parse(dealerListHtml);
            Elements dealerListElement = dealerListDocument.getElementsByClass("row nets nets-div");
            for (Element dealerElement : dealerListElement) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByClass("col-md-12 col-xs-12 nets-title").first().text();
                String text = dealerElement.getElementsByClass("col-md-12 col-xs-12 nets-contact")
                        .first().getElementsByTag("p").first().text()
                        .replaceAll("销售热线：","sellPhone：")
                        .replaceAll("销 售 热 线：","sellPhone：")
                        .replaceAll("销售电话：","sellPhone：")
                        .replaceAll("销售服务热线：","sellPhone：")
                        .replaceAll("贵宾专享热线：","sellPhone：")
                        .replaceAll("销售热线: ","sellPhone：")
                        .replaceAll("咨询电话：","sellPhone：")
                        .replaceAll("购车热线：","sellPhone：")
                        .replaceAll("销售热线:","sellPhone：")
                        .replaceAll("电话：（0731）","sellPhone：（0731）");
                try {
                    String address=text.split("地址：")[1].split(" ")[0].trim();
                    carDealer.setDealerAddress(address);
                    //城市正则
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        if(address!=null&&!address.equals("")){
                            carDealer.setCityName(address.substring(0,2));
                        }else {
                            carDealer.setCityName(dealerName.substring(0,2));
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    carDealer.setDealerAddress("");
                }
                if(text.indexOf("sellPhone：")!=-1){
                    text=text.replaceAll("售|服|转1|地"," ");
                    String[] s = text.split("sellPhone：")[1].split(" ");
                    try {
                        String phone=s[0]+" "+s[1];
                        carDealer.setDealerPhone(phone);
                    }catch (ArrayIndexOutOfBoundsException e){
                        String phone=s[0];
                        carDealer.setDealerPhone(phone);
                    }
                }
                carDealer.setBrandName(brandName);
                carDealer.setDealerName(dealerName);
                if(carDealer.getCityName().equals("")){
                    carDealer.setCityName(dealerName.substring(0,2));
                }
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //远通汽车集团
    @Test
    void testGetYuantongqichejituan() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="远通汽车经销商数据.xlsx";
        String indexUrl="http://www.lyqgm.com/dealerships";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("col-sm-6");
        for (Element element : dealerList) {
            CarDealer carDealer=new CarDealer();
            String dealerName = element.getElementsByTag("h4").first().text();
            String dealerAddress = element.getElementsByClass("media-body").first().getElementsByTag("p").first().text();
            String phone = element.getElementsByClass("media-body").first()
                    .getElementsByTag("p").get(1).text().split("售前电话：")[1];
            String brandName = element.getElementsByClass("media-object").first().attr("alt");
            //城市正则
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(dealerAddress);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                if(dealerAddress!=null&&!dealerAddress.equals("")){
                    carDealer.setCityName(dealerAddress.substring(0,2));
                }else {
                    carDealer.setCityName(dealerName.substring(0,2));
                }
            }
            carDealer.setDealerPhone(phone);
            carDealer.setDealerName(dealerName);
            carDealer.setBrandName(brandName);
            carDealer.setDealerAddress(dealerAddress);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }
}
