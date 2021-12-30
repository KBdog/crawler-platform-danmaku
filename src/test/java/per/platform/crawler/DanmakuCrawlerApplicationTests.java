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
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
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
    private OkHttpClient noProxyClient;

    @Autowired
    private WebDriver driver;

    //2021年12月23日09:28:28 新增目录
    private String excelOutPutDir="C:\\Users\\Lenovo\\Desktop\\work\\issue7\\part2\\";

    //2021年12月27日18:13:57 新增part3目录
    private String excelOutPutDirPart3="C:\\Users\\Lenovo\\Desktop\\work\\issue7\\part3\\";

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
        List<String>urlList=new ArrayList<>();
        urlList.add("https://www.rocar.net/productList/brand?brand=bz&menu_id=12");
        urlList.add("https://www.rocar.net/productList/brand?brand=xd&menu_id=12");
        urlList.add("https://www.rocar.net/productList/brand?brand=bj&menu_id=12");
        urlList.add("https://www.rocar.net/productList/brand?brand=jh&menu_id=12");
        for (String url : urlList) {
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
                for (Element aUrl : a1) {
                    String dealerUrl="http://www.pdqmjt.com/"+aUrl.attr("href");
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
                }
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

    //润华汽车
    @Test
    void testGetRunhuaqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="润华汽车经销商数据.xlsx";
        String indexUrl="http://car.runhua.com.cn/list/?7_1.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements brandList = document.getElementsByClass("net-list clear").first().getElementsByTag("li");
        for(int i=0;i<15;i++){
            Element li = brandList.get(i);
            String brandName=li.getElementsByTag("img").first().attr("alt");
            String dealerListUrl="http://car.runhua.com.cn"+li.getElementsByTag("a").first().attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerListUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerListHtml = rsp1.body().string();
            Document dealerListDocument = Jsoup.parse(dealerListHtml);
            Elements liList = dealerListDocument.getElementsByClass("netAddr-list").first().getElementsByTag("li");
            for (Element liElement : liList) {
                log.info(liElement.text());
                CarDealer carDealer=new CarDealer();
                String[] messageContentArray = liElement.text().split(" ");
                String dealerName=messageContentArray[1].split("注册名：")[1];
                String address=messageContentArray[2].split("地址：")[1];
                String phone=null;
                try {
                    phone=messageContentArray[3].split("销售电话：")[1];
                }catch (ArrayIndexOutOfBoundsException e){
                    phone=messageContentArray[4].split("销售电话：")[1];
                }
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
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //威佳汽车
    @Test
    void testGetWeijiaqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="威佳汽车经销商数据.xlsx";
        /**
         * 先访问首页获取所有品牌，再通过对应url获得js内容解析成json
         */
        //获取所有品牌value
        String indexUrl="http://www.weijiajituan.com/index/index/network.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml=response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Element selectbrand = document.getElementById("selectbrand");
        Elements option = selectbrand.getElementsByTag("option");
        List<String>brandList=new ArrayList<>();
        for (Element element : option) {
            String brandName = element.attr("value");
            if(brandName!=null&&!brandName.equals("")){
                brandList.add(brandName);
            }
        }
        for (String brandName : brandList) {
            String url="http://www.weijiajituan.com/index/index/network.html?city=&brand="+brandName;
            Response rsp = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerListHtml = rsp.body().string();
            Document dealerListDocument = Jsoup.parse(dealerListHtml);
            //倒数第七个script
            Elements script = dealerListDocument.getElementsByTag("script");
            Element last = script.get(script.size() - 7);
            String text=last.html().replaceAll(" ","")
                    .split("varLocations=")[1].split("window\\.onload=")[0].trim();
            String jsonString=text.substring(0,text.length()-1);
            JSONArray jsonArray = JSONArray.parseArray(jsonString);
            if(jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CarDealer carDealer=new CarDealer();
                    String address = jsonObject.getString("address");
                    String phone = jsonObject.getString("phone");
                    String dealerName = jsonObject.getString("name");
                    //城市正则
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        if(address!=null&&!address.equals("")){
                            carDealer.setCityName(dealerName.substring(0,3));
                        }else {
                            carDealer.setCityName(address.substring(0,3));
                        }
                    }
                    carDealer.setBrandName(brandName);
                    carDealer.setDealerName(dealerName);
                    carDealer.setDealerPhone(phone);
                    carDealer.setDealerAddress(address);
                    resultList.add(carDealer);
                }
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //惠通陆华
    @Test
    void testGetHuitongluhua() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="惠通陆华经销商数据.xlsx";
        String url="https://dealer.landrover.com.cn/index.php?s=/LRDealer/api/getDealerList&is_extend=21&is_lack=1";
        Response response = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
        String jsonString = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray data = jsonObject.getJSONArray("data");
        String brandName="路虎";
        for(int i=0;i<data.size();i++){
            JSONObject dealerContent = data.getJSONObject(i);
            CarDealer dealer=new CarDealer();
            String dealerName = dealerContent.getString("dealer_name");
            String dealerAddress = dealerContent.getString("addr");
            String phone = dealerContent.getString("sales_phone_landrover");
            String cityName = dealerContent.getString("city_name");
            dealer.setDealerPhone(phone);
            dealer.setCityName(cityName);
            dealer.setDealerAddress(dealerAddress);
            dealer.setDealerName(dealerName);
            dealer.setBrandName(brandName);
            resultList.add(dealer);

        }
        exportExcel(excelOutPutDir,companyName,resultList);

    }

    //湖南永通
    @Test
    void testGetHunanyongtong() throws IOException{
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="湖南永通经销商数据.xlsx";
        String indexUrl="http://www.hnytcar.com/index/Index/brand/id/1.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Element brandListElement = document.getElementsByClass("logo_box12").first();
        Elements brandList = brandListElement.getElementsByTag("li");
        for (Element li : brandList) {
            String dealerUrl="http://www.hnytcar.com"+li.getElementsByTag("a").first().attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String brandHtml = rsp1.body().string();
            Document brandDocument = Jsoup.parse(brandHtml);
            String brandName = brandDocument.getElementsByClass("listheads").first().text();
            Elements title = brandDocument.getElementsByClass("right_title");
            Elements text = brandDocument.getElementsByClass("right_txt");
            for(int i=0;i<title.size();i++){
                CarDealer carDealer=new CarDealer();
                Element element = title.get(i);
                String dealerName=element.text();
                Element textContent = text.get(i);
                Elements pList = textContent.getElementsByTag("p");
                String address = pList.get(0).text().replaceAll("地　　址：","");
                String phone = pList.get(1).text();
                //城市正则
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //和谐汽车
    @Test
    void testGetHexieqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="和谐汽车经销商数据.xlsx";
        String indexUrl="http://www.hexieauto.com/brand";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements brandList = document.getElementsByClass("brand-item");
        for (Element brand : brandList) {
            String brandName = brand.getElementsByClass("brand-title clearfix").first().text();
            Elements dealerList = brand.getElementsByClass("brand-content-right");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByClass("name").first().text();
                String phone=dealerElement.getElementsByClass("row2").first()
                        .text().trim().replaceAll("电话：","");
                String address=dealerElement.getElementsByClass("row4").first()
                        .text().trim().replaceAll("地址：","");
                //城市正则
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setBrandName(brandName);
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //兰天集团
    @Test
    void testGetLantianjituan() throws IOException {
        //品牌列表
        List<String>brandList=new ArrayList<>();
        brandList.add("东风日产");
        brandList.add("东风本田");
        brandList.add("别克");
        brandList.add("东风雪铁龙");
        brandList.add("东风标致");
        brandList.add("东风启辰");
        brandList.add("上汽大众");
        brandList.add("一汽大众");
        brandList.add("凯迪拉克");
        brandList.add("奔驰");
        brandList.add("雷诺");
        brandList.add("通用雪佛兰");
        brandList.add("上汽斯柯达");
        brandList.add("东风悦达起亚");
        brandList.add("英菲尼迪");
        brandList.add("吉利");
        brandList.add("长安铃木");
        brandList.add("广汽三菱");
        brandList.add("上汽大通");
        brandList.add("奇瑞");
        brandList.add("一汽奔腾");
        brandList.add("长丰猎豹");
        brandList.add("众泰大迈");
        brandList.add("芝麻");
        brandList.add("云度");
        brandList.add("奥拓");
        brandList.add("捷豹路虎");
        brandList.add("上汽荣威");
        brandList.add("捷豹路虎");
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="兰天集团经销商数据.xlsx";
        String indexUrl="http://www.hnlantian.com.cn/intro/8.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByTag("tbody").get(1).getElementsByTag("td");
        for (Element dealer : dealerList) {
            try {
                CarDealer carDealer=new CarDealer();
                String name=null;
                try {
                    name=dealer.getElementsByTag("div").first().text();
                }catch (NullPointerException e){
                    name = dealer.getElementsByTag("p").first().text();
                }
                String brandName=null;
                //确认品牌名
                for (String brand : brandList) {
                    if(name.indexOf(brand)!=-1){
                        brandName=brand;
                        break;
                    }
                }
                if(brandName==null){
                    brandName="";
                }
                String[] messageContent = dealer.text().replaceAll(name, "").trim().split("&nbsp;");
                if(!name.equals("")){
                    System.out.println(name+"-----"+messageContent[0]);
                    String phone=messageContent[0].split(" ")[0].replaceAll("销售热线：","");
                    String address=messageContent[0].split(" ")[2].replaceAll("地址：","");
                    if(name.equals("长安铃木兰天城西4S店")){
                        phone=messageContent[0].split(" ")[0].replaceAll("销售电话：","");
                        address=messageContent[0].split(" ")[3].replaceAll("地址：","");
                    }
                    if(name.equals("东风日产兰天株洲河西4S店")){
                        String[] messageArray = messageContent[0].split(" ");
                        phone=(messageArray[0]+messageArray[1]).replaceAll("销售热线：","");
                        address=messageContent[0].split(" ")[4].replaceAll("地址：","");
                    }
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        carDealer.setCityName(address.substring(0,2));
                    }
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerPhone(phone);
                    carDealer.setBrandName(brandName);
                    carDealer.setDealerName(name);
                    resultList.add(carDealer);
                }
            }catch (NullPointerException e){
                continue;
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //博行汽车
    @Test
    void testGetBoxingqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="博行汽车经销商数据.xlsx";
        String indexUrl="http://www.dx-home.com/dealernetwork.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements areaBox = document.getElementsByClass("areaBox");
        for(int i=1;i<areaBox.size();i++){
            try {
                Element box = areaBox.get(i);
                String city=box.getElementsByClass("areaTitle").first().text();
                if(city.equals("")){
                    city=box.getElementsByClass("areaTitle").get(2).text();
                }
                Elements areaContent = box.getElementsByClass("areaContent");
                for (Element dealerElement : areaContent) {
                    CarDealer carDealer=new CarDealer();
                    String[] contentArray = dealerElement.text().split(" ");
                    String dealerName=contentArray[0];
                    String brandName=contentArray[1].replaceAll("品牌：","");
                    String address=contentArray[2].replaceAll("地址：","");
                    String phone=contentArray[3].replaceAll("电话：","");
                    carDealer.setDealerName(dealerName);
                    carDealer.setBrandName(brandName);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerPhone(phone);
                    carDealer.setCityName(city);
                    resultList.add(carDealer);
                }
            }catch (NullPointerException e){
                continue;
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }


    //天津捷通达
    @Test
    void testGetTianjinjietongda() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="天津捷通达经销商数据.xlsx";
        List<String>urlList=new ArrayList<>();
        //天津
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=24&start=0");
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=24&start=1");
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=24&start=2");
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=24&start=3");
        //云南
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=25&start=0");
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=25&start=1");
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=25&start=2");
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=25&start=3");
        //辽宁
        urlList.add("https://www.jetonda.com/fac.php?id=4&sid=34&start=0");
        for (String url : urlList) {
            driver.get(url);
            Document document = DriverTools.parseCurrentWebPage(15, driver);
            Elements select = document.select("table[width=100%]table[border=0]table[cellspacing=0]table[cellpadding=0]");
            for(int i=1;i<select.size();i+=2){
                Element element = select.get(i);
                CarDealer carDealer=new CarDealer();
                String []contentArray = element.text().split(" ");
                String dealerName=contentArray[0];
                String brandName=contentArray[1].replaceAll("品牌：","");
                String address=contentArray[2].replaceAll("地址：","");
                String phone=contentArray[3].replaceAll("电话：","");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setBrandName(brandName);
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //新丰泰集团
    @Test
    void testGetXinfengtai() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="新丰泰集团经销商数据.xlsx";
        String url="http://www.sunfonda.com.cn/cn/business/brandportfolio.html";
        Response response = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass(" shown padding-box");
        //品牌集合
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095609015.jpg","宾利");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095599599.jpg","保时捷");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095657579.jpg","奔驰");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095668033.jpg","宝马");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095619602.jpg","奥迪");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095646501.jpg","雷克萨斯");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095628817.jpg","凯迪拉克");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095637816.jpg","大众");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095702486.jpg","红旗");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095742833.jpg","广汽丰田");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095692547.jpg","一汽丰田");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095751482.jpg","广汽本田");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095712369.jpg","一汽大众");
        brandMap.put("http://sunfonda.oss-cn-qingdao.aliyuncs.com/upload/2020-04/1587095679252.jpg","上汽大众");
        for (Element dealerElement : dealerList) {
            CarDealer carDealer=new CarDealer();
            //判断品牌
            String href = dealerElement.getElementsByTag("img").first().attr("src");
            String brandName="";
            for(Map.Entry<String,String>brand:brandMap.entrySet()){
                if(href.equals(brand.getKey())){
                    brandName=brand.getValue();
                    break;
                }
            }
            String dealerName = dealerElement.getElementsByTag("h3").first().text();
            String address = dealerElement.getElementsByTag("ui").get(1).text().replaceAll("地址：", "");
            String phone = dealerElement.getElementsByTag("ui").get(2).text().replaceAll("服务热线：", "");
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                carDealer.setCityName(address.substring(0,2));
            }
            carDealer.setDealerName(dealerName);
            carDealer.setDealerAddress(address);
            carDealer.setDealerPhone(phone);
            carDealer.setBrandName(brandName);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //顺骋集团
    @Test
    void testGetShunchengjituan() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="顺骋集团经销商数据.xlsx";
        String url="https://www.sdshuncheng.com/service/";
        Response response = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements divList = document.getElementById("portfoliolist").getElementsByTag("div");
        for(int i=1;i<divList.size();i++){
            Element div = divList.get(i);
            String[] messageArray = div.text().split(" ");
            CarDealer carDealer=new CarDealer();
            String dealerName=messageArray[0];
            String address=messageArray[1].replaceAll("门店地址：","");
            String phone=messageArray[3].replaceAll("联系方式：","");
            String brandName=messageArray[4].replaceAll("品牌：","");
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                carDealer.setCityName(address.substring(0,2));
            }
            carDealer.setBrandName(brandName);
            carDealer.setDealerPhone(phone);
            carDealer.setDealerName(dealerName);
            carDealer.setDealerAddress(address);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //厦门建发
    @Test
    void testGetXiamenjianfa() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="厦门建发经销商数据.xlsx";
        String indexUrl="http://www.autocnd.com/network.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Map<String,String>brandMap=new HashMap<>();
        Elements brandList = document.getElementsByClass("tb");
        for (Element brandElement : brandList) {
            String brandId = brandElement.attr("name");
            String brandName = brandElement.getElementsByTag("img").first().attr("alt");
            brandMap.put(brandId,brandName);
        }
        String url="http://www.autocnd.com/network.html";
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName=brand.getValue();
            Map<String,String>paramMap=new HashMap<>();
            paramMap.put("brand",brand.getKey());
            Response rsp1 = OkHttpTools.postResponseFromForm(url, paramMap, Constant.DEFAULT_HEADERS_BUILDER, client);
            String jsonString = rsp1.body().string();
            JSONArray jsonArray = JSONArray.parseArray(jsonString);
            for(int i=0;i<jsonArray.size();i++){
                String regex="地址：|展厅地址：|销售电话：|销售热线：|电话：|贵宾热线：|贵宾专线：";
                CarDealer carDealer=new CarDealer();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dealerName = jsonObject.getString("name");
                if(!jsonObject.getString("content").equals("敬请期待")){
                    String[] messageArray = jsonObject.getString("content")
                            .replaceAll(regex,"").split("<Br>");
                    String address=messageArray[0];
                    String phone=messageArray[1];
                    if(dealerName.equals("厦门威马用户中心湖里店")){
                        phone=messageArray[2];
                    }
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        carDealer.setCityName(address.substring(0,2));
                    }
                    carDealer.setDealerPhone(phone);
                    carDealer.setDealerAddress(address);
                }
                carDealer.setBrandName(brandName);
                carDealer.setDealerName(dealerName);
                resultList.add(carDealer);
            }

        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //河北诚实实业
    @Test
    void testGetChengshishiye() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="河北诚实实业经销商数据.xlsx";
        String indexUrl="http://www.hebcs.com/business.aspx?type=8";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements liList = document.getElementsByClass("hd").first().getElementsByTag("li");
        String url="http://www.hebcs.com/WebUserControl/business/More.ashx";
        for (Element element : liList) {
            String brandName=element.text();
            Map<String,String>paramMap=new HashMap<>();
            paramMap.put("method","GetListMore");
            paramMap.put("id",element.attr("data-id"));
            paramMap.put("cla",element.attr("data-video"));
            Response rsp1 = OkHttpTools.postResponseFromForm(url, paramMap, Constant.DEFAULT_HEADERS_BUILDER, client);
            String jsonString = rsp1.body().string();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String list = jsonObject.getString("List");
            Document dealerListDocument = Jsoup.parse(list);
            Elements cityList = dealerListDocument.getElementsByClass("hd2").first().getElementsByTag("li");
            Elements dealerList = dealerListDocument.getElementsByClass("bd2").first().getElementsByTag("li");
            for(int i=0;i<cityList.size();i++){
                CarDealer carDealer=new CarDealer();
                Element cityElement = cityList.get(i);
                Element dealerElement = dealerList.get(i);
                String city = cityElement.text();
                String dealerName = dealerElement.getElementsByTag("h3").first().text();
                String address = dealerElement.getElementsByTag("p")
                        .first().text().replaceAll("地址：","");
                String phone=dealerElement.getElementsByTag("p").get(1)
                        .text().replaceAll("电话：","");
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                carDealer.setCityName(city);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerName(dealerName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //临沂易通
    @Test
    void testGetLinyiyitong() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="临沂易通经销商数据.xlsx";
        String indexUrl="http://lyytqm.com/dealerships";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("col-12 col-md-6");
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/3szyfgu4.jpg","英菲尼迪");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/18/njpaemh4.jpg","宝马");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/16/amfl1oor.jpeg","凯迪拉克");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/18/5s5kblz5.jpg","东风日产");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/magkzjfh.jpg","东风启辰");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/45v3lolr.jpg","海马");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/o2bvsjfu.jpg","东风风神");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/11/12/zhsrc3qr.jpg","长安欧尚");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/44b0whjm.jpg","北京现代");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/py42tboo.jpg","上汽通用别克");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/22/qvbvtafo.jpg","上汽荣威");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/4mglkcbd.jpg","广汽本田");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/16/soxpgm1q.jpeg","广汽传祺");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/16/qxdjh25e.jpg","一汽马自达");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/22/nxljoxol.jpg","长安福特");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/mb4rrejq.jpg","东风本田");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/25/phizulcz.jpg","东风标致");
        brandMap.put("http://assets.abpone.com/66a5b8fdfb9e836de22d39f6256a21aa/53dafcc54d8f4ae4b53841720d24f9b8/2020/09/16/jr3t3b1j.jpg","东风雪铁龙");
        for (Element dealerElement : dealerList) {
            CarDealer carDealer=new CarDealer();
            String imgUrl = dealerElement.getElementsByTag("img").first().attr("src");
            for(Map.Entry<String,String>brand:brandMap.entrySet()){
                if(imgUrl.equals(brand.getKey())){
                    carDealer.setBrandName(brand.getValue());
                    break;
                }
            }
            String dealerName = dealerElement.getElementsByClass("media-body ml-3")
                    .first().getElementsByTag("h5").first().text();
            String regex="地址位置：|热线电话：";
            String text = dealerElement.getElementsByClass("media-body ml-3").first()
                    .getElementsByTag("p").first().text().replaceAll(regex,"")
                    .replaceAll("－","-").trim();
            String []messageArray=text.split(" ");
            String phone=messageArray[0];
            if(phone.indexOf("-")==-1){
                phone=messageArray[0]+messageArray[1];
                String address=messageArray[3];
                String city=address.substring(0,2);
                carDealer.setCityName(city);
                carDealer.setDealerAddress(address);
                carDealer.setDealerPhone(phone);
            }else {
                String address=messageArray[2];
                String city=address.substring(0,2);
                carDealer.setCityName(city);
                carDealer.setDealerAddress(address);
                carDealer.setDealerPhone(phone);
            }
            carDealer.setDealerName(dealerName);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //华宏汽车
    @Test
    void testGetHuahongqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="华宏汽车经销商数据.xlsx";
        String indexUrl="http://www.hhqc.cn/Us/sub_company";
        List<String>brandList=new ArrayList<>();
        brandList.add("奥迪");
        brandList.add("奔驰");
        brandList.add("北京现代");
        brandList.add("一汽-大众");
        brandList.add("凯迪拉克");
        brandList.add("别克");
        brandList.add("广汽丰田");
        brandList.add("一汽红旗");
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("distribution_store_cont");
        for (Element dealerElement : dealerList) {
            CarDealer carDealer=new CarDealer();
            String dealerName = dealerElement.getElementsByClass("p1 size3").first().text();
            String address = dealerElement.getElementsByClass("p2 size4").first().text();
            String phone = dealerElement.getElementsByClass("p3 size4").first().text();
            for (String brand : brandList) {
                if(dealerName.indexOf(brand)!=-1){
                    carDealer.setBrandName(brand);
                }
            }
            String cityRegex="^(.*(市|区))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                carDealer.setCityName(address.substring(0,2));
            }
            carDealer.setDealerPhone(phone);
            carDealer.setDealerAddress(address);
            carDealer.setDealerName(dealerName);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //轿辰集团
    @Test
    void testGetJiaochenjituan() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="轿辰集团经销商数据.xlsx";
        String indexUrl="http://www.jiaochen.com.cn/Index/brand_detail/pid/5.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("shop_item w_100");
        for (Element dealerElement : dealerList) {
            String brandName = dealerElement.getElementsByTag("b").first().text();
            Elements table = dealerElement.getElementsByClass("hove").first().getElementsByTag("table");
            for (Element dealer : table) {
                CarDealer carDealer=new CarDealer();
                String regex="&nbsp;|销售热线：|销售电话：|公司地址：|公司地址:";
                Element td = dealer.getElementsByClass("firstRow")
                        .first().getElementsByTag("td").first();
                String dealerName = td.getElementsByTag("p").first().text();
                String address = td.getElementsByTag("p").get(1)
                        .text().replaceAll(regex,"");
                String phone = td.getElementsByTag("p")
                        .get(2).text().replaceAll(regex,"");
                String cityRegex="^(.*(市|区))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setBrandName(brandName);
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);

    }

    //沈阳大众
    @Test
    void testGetShenyangdazhong() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="沈阳大众经销商数据.xlsx";
        List<String>indexUrlList=new ArrayList<>();
        indexUrlList.add("http://www.sdzgroup.com/business.php?tid=1&pageno=1");
        indexUrlList.add("http://www.sdzgroup.com/business.php?tid=1&pageno=2");
        Map<String,List<String>>brandMap=new HashMap<>();
        for (String indexUrl : indexUrlList) {
            Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements liList = document.getElementsByClass("brand_ul").first().getElementsByTag("li");
            for (Element li : liList) {
                List<String>requestPageList=new ArrayList<>();
                String brandUrl="http://www.sdzgroup.com/"+li.getElementsByTag("a")
                        .first().attr("href");
                String brandName=li.getElementsByTag("a")
                        .first().attr("title");
                requestPageList.add(brandUrl);
                brandMap.put(brandName,requestPageList);
            }
        }
        for(Map.Entry<String,List<String>>brand:brandMap.entrySet()){
//            String brandName=brand.getKey();
            List<String> requestPageList = brand.getValue();
            String brandUrl = requestPageList.get(0);
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            String pag = document.getElementsByClass("pag").first().text().trim();
            if(pag.equals("本栏目暂无资料，感谢您的关注..")||pag.equals("")){
                continue;
            }else {
                Elements aList = document.getElementsByClass("pag").first().getElementsByTag("a");
                if(aList.size()>0){
                    String page=aList.get(aList.size()-2).text();
                    int pageNum = Integer.parseInt(page);
                    for(int i=2;i<=pageNum;i++){
                        String nextPageUrl=brandUrl+"&pageno="+i;
                        requestPageList.add(nextPageUrl);
                    }
                }
            }
        }
        for(Map.Entry<String,List<String>>brand:brandMap.entrySet()){
            String brandName=brand.getKey();
            List<String> brandUrlList = brand.getValue();
            System.out.println(brandName+"---"+brandUrlList.toString());
            for (String brandUrl : brandUrlList) {
                Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String pageHtml = response.body().string();
                Document document = Jsoup.parse(pageHtml);
                Elements dealerList = document.getElementsByClass("brand_all");
                for (Element dealerElement : dealerList) {
                    CarDealer carDealer=new CarDealer();
                    String regex="地址：| 服务热线：|地址:| 服务热线:";
                    String dealerName = dealerElement.getElementsByClass("Span_title").first().text();
                    String []messageArray=dealerElement.getElementsByClass("Span_con")
                            .first().text().replaceAll(regex,"").split(" ");
                    String address=messageArray[0];
                    String phone = messageArray[1];
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        carDealer.setCityName(address.substring(0,2));
                    }
                    carDealer.setDealerPhone(phone);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerName(dealerName);
                    carDealer.setBrandName(brandName);
                    resultList.add(carDealer);
                }
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //湖南力天
    @Test
    void testGetHunanlitian() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="湖南力天经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("宝马","http://www.ltauto.cn/product/7/");
        brandMap.put("林肯","http://www.ltauto.cn/product/8/");
        brandMap.put("雷克萨斯","http://www.ltauto.cn/product/9/");
        brandMap.put("丰田","http://www.ltauto.cn/product/10/");
        brandMap.put("福特","http://www.ltauto.cn/product/11/");
        brandMap.put("阿尔法·罗密欧","http://www.ltauto.cn/product/12/");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName=brand.getKey();
            String brandUrl=brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass("e_box d_titleBox p_titleBox_3 ");
            for (Element element : dealerList) {
                CarDealer carDealer=new CarDealer();
                String regex="地址 |电话 ";
                String dealerUrl="http://www.ltauto.cn"+element.getElementsByTag("a")
                        .first().attr("href");
                String dealerName = element.getElementsByTag("h3").first().text();
                Response rsp1 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String dealerHtml = rsp1.body().string();
                Document dealerDocument = Jsoup.parse(dealerHtml);
                String messageContent=dealerDocument.getElementsByClass("e_box e_ParameterBox-001 d_BaseInfoBox p_BaseInfoBox js_attrOne")
                        .first().text().replaceAll(regex,"").trim();
                String address=messageContent.split(" ")[0];
                String phone=messageContent.split(" ")[1];
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //金阳光集团
    @Test
    void testGetJinyangguang() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="金阳光集团经销商数据.xlsx";
        String indexUrl="https://www.jyggroup.cn/brand/class/";
        driver.get(indexUrl);
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("right benchi","奔驰");
        brandMap.put("right xiandai","现代");
        brandMap.put("right bieke","别克");
        brandMap.put("right chuanqi","传祺");
        brandMap.put("right hongqi","红旗");
        brandMap.put("right fengtian","丰田");
        brandMap.put("right bentian","本田");
        brandMap.put("right aodi","奥迪");
        brandMap.put("right leikesasi","雷克萨斯");
        brandMap.put("right dazhong","大众");
        Document document = DriverTools.parseCurrentWebPage(15, driver);
        Elements cityList = document.getElementById("province").getElementsByTag("option");
        for (Element cityElement : cityList) {
            String cityName = cityElement.text();
            String cityUrl="https://www.jyggroup.cn/"+cityElement.attr("value").replaceAll("\\.\\./\\.\\./","")
                    .replaceAll("&key=","");
//            System.out.println(cityName+"--"+cityUrl);
            driver.get(cityUrl);
            Document cityDocument = DriverTools.parseCurrentWebPage(15, driver);
            Elements dealerList = cityDocument.getElementsByClass("pinpaiquer");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String className=dealerElement.getElementsByTag("div").get(2).attr("class");
                System.out.println(className);
                //判断品牌
                for(Map.Entry<String,String>brand:brandMap.entrySet()){
                    if(className.equals(brand.getKey())){
                        carDealer.setBrandName(brand.getValue());
                        break;
                    }
                }
                String regex="地址 : |销售热线 : ";
                Element messageElement = dealerElement.getElementsByClass("center").first();
                String dealerName = messageElement.getElementsByTag("h3").first().text();
                String address = messageElement.getElementsByTag("font").first().text().replaceAll(regex,"");
                String phone = messageElement.getElementsByTag("font").get(1).text().replaceAll(regex,"");
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setDealerName(dealerName);
                carDealer.setCityName(cityName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //山西大昌
    @Test
    void testGetShanxidachang() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="山西大昌经销商数据.xlsx";
        String indexUrl="https://www.sxdachang.com/dc-car-business.html?id=64";
        Map<String,String>cityMap=new HashMap<>();
        cityMap.put("taiYuan","太原市");
        cityMap.put("daTong","大同市");
        cityMap.put("xinZhou","忻州");
        cityMap.put("lvLiang","吕梁市");
        cityMap.put("jinZhong","晋中市");
        cityMap.put("changZhi","长治市");
        cityMap.put("linFen","临汾市");
        cityMap.put("yunCheng","运城市");
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/07-30/17-56-330157-459515881.png","红旗");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-33-240531-1772876325.jpg","福特");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-33-570665-1064506814.jpg","一汽丰田");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-40-100968143226738.jpg","一汽大众");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-38-3404271146313205.jpg","奥迪");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-47-0905161609661595.jpg","东风日产");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-56-120155-443491374.jpg","广汽丰田");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/10-58-160704593581748.jpg","雷克萨斯");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/11-03-210381321090540.jpg","斯柯达");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/11-29-0906651672560374.jpg","一汽奔腾");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/15-00-060549708612173.jpg","捷达");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/15-28-27063794181146.jpg","雪佛兰");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/15-27-180493-117472430.jpg","别克");
        brandMap.put("https://www.sxdachang.com/webfile/upload/2021/08-20/14-58-310250-370007668.jpg","广汽丰田");
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("dcZgsItem");
        for (Element dealerElement : dealerList) {
            CarDealer carDealer=new CarDealer();
            String city = dealerElement.attr("name");
            for(Map.Entry<String,String>cityData:cityMap.entrySet()){
                if(city.equals(cityData.getKey())){
                    carDealer.setCityName(cityData.getValue());
                    break;
                }
            }
            String imgSrc = dealerElement.getElementsByClass("dcZgsBz").first().attr("src");
            for(Map.Entry<String,String>brand:brandMap.entrySet()){
                if(imgSrc.equals(brand.getKey())){
                    carDealer.setBrandName(brand.getValue());
                    break;
                }
            }
            String dealerName = dealerElement.getElementsByClass("dcZgsTitle").first().text();
            Elements pList = dealerElement.getElementsByClass("dcZgsDec").first().getElementsByTag("p");
            String address = pList.get(0).getElementsByTag("span").first().text();
            String phone = pList.get(1).getElementsByTag("span").first().text();
            carDealer.setDealerPhone(phone);
            carDealer.setDealerAddress(address);
            carDealer.setDealerName(dealerName);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //和通汽车
    @Test
    void testGetHetongqiche() throws InterruptedException, IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="和通汽车经销商数据.xlsx";
        String indexUrl="http://www.hotongmotor.com/map";
        driver.get(indexUrl);
        System.out.println("开始休眠,等待页面操作");
        Thread.sleep(3000);
        System.out.println("休眠完毕,开始抓取页面");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //分钟
        int minutes=3;
        long startTimeMillis = System.currentTimeMillis();
        long endTimeMillis=startTimeMillis+(minutes*60*1000);
        String startTime = format.format(startTimeMillis);
        String endTime=format.format(endTimeMillis);
        log.info("开始时间:"+startTime+"--结束时间:"+endTime);
        long currentTimeMillis=new Date().getTime();
        while(currentTimeMillis<endTimeMillis){
            currentTimeMillis=new Date().getTime();
            Document document = DriverTools.parseCurrentWebPage(15, driver);
            String brandName=document.getElementsByClass("btn-default dropdown-toggle btn1").first().text();
            Elements dealerList = document.getElementsByClass("nav-item list_b");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String regex="getMap\\(|\\)|'";
                String dealerName = dealerElement.text();
                String[]messageArray=dealerElement.getElementsByTag("a").first()
                        .attr("onclick").replaceAll(regex,"").split(",");
                String address=messageArray[0];
                String city=messageArray[1];
                String phone=messageArray[2];
                carDealer.setDealerPhone(phone);
                carDealer.setCityName(city);
                carDealer.setDealerAddress(address);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
            if(brandName.equals("广汽丰田")){
                break;
            }
            System.out.println(brandName+" 抓取完毕,开始休眠六秒钟");
            Thread.sleep(6000);
            System.out.println("休眠完毕,开始下一轮抓取");
        }
        System.out.println("开始导出数据");
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //红旭集团
    @Test
    void testGetHongxujituan() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="红旭集团经销商数据.xlsx";
        String indexUrl="http://www.hongxu.cn/network.aspx";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements brandList = document.getElementsByClass("pro_item")
                .first().getElementsByTag("li");
        for (Element brandElement : brandList) {
            try {
                CarDealer carDealer=new CarDealer();
                String brandName = brandElement.text();
                String brandUrl="http://www.hongxu.cn/"+brandElement.getElementsByTag("a")
                        .first().attr("href");
                Response rsp1 = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String brandHtml = rsp1.body().string();
                Document brandDocument = Jsoup.parse(brandHtml);
                Elements list = brandDocument.getElementsByClass("pro_viewlist");
                for (Element element : list) {
                    Element messageContent = element.getElementsByClass("pro_viewtext po").first();
                    String dealerName=messageContent.getElementsByTag("p").first()
                            .text().replaceAll("全称：","");
                    String address=messageContent.getElementsByTag("p").get(1).text()
                            .replaceAll("地址：| 售后服务：","").split(" ")[0];
                    String phone=messageContent.getElementsByTag("p").get(1).text()
                            .replaceAll("地址：|售后服务：","").split(" ")[1];
                    String cityRegex="^(.*(市|区))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        carDealer.setCityName(address.substring(0,2));
                    }
                    carDealer.setBrandName(brandName);
                    carDealer.setDealerName(dealerName);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerPhone(phone);
                    resultList.add(carDealer);
                }
            }catch (NullPointerException e){
                continue;
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //河北国和
    @Test
    void testGetHebeiguohe() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="河北国和经销商数据.xlsx";
        String indexUrl="http://www.hbsgh.com/wscs_pinpai.jsp#";
        //电话和地址为空的经销商
        List<String>defaultDealerList=new ArrayList<>();
        defaultDealerList.add("邢台国和同运汽车贸易有限公司");
        defaultDealerList.add("石家庄荟萃汽车销售技术服务有限公司");
        defaultDealerList.add("河北国和腾朗汽车销售服务有限公司");
        defaultDealerList.add("邯郸市盛华汽车贸易有限公司");
        defaultDealerList.add("河北众捷汽车贸易有限公司");
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.select("div[id=wscs_quyu_dealer_right]");
        for (Element dealerElement : dealerList) {
            String regex="主营品牌：|销售热线：|地 址：";
            String []messageArray=dealerElement.text().replaceAll(regex,"").split(" ");
            CarDealer carDealer=new CarDealer();
            String dealerName=messageArray[0];
            String brandName=messageArray[1];
            if(defaultDealerList.contains(dealerName)){
                carDealer.setDealerAddress("");
                carDealer.setDealerPhone("");
            }else {
                carDealer.setDealerAddress(messageArray[5]);
                carDealer.setDealerPhone(messageArray[3]);
            }
            if(!carDealer.getDealerAddress().equals("")){
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(carDealer.getDealerAddress());
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }
            }
            carDealer.setBrandName(brandName);
            carDealer.setDealerName(dealerName);
            resultList.add(carDealer);
            System.out.println(dealerElement.text());
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //庆丰汽车
    @Test
    void testGetQingfengqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="庆丰汽车经销商数据.xlsx";
        String indexUrl="http://www.qfcars.com/Content/462668.html";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("col-xs-7 col-md-6 ModuleImageTextGiantContent ModuleImageTextContent ");
        for (Element dealerElement : dealerList) {
            CarDealer carDealer=new CarDealer();
            Elements pList = dealerElement.getElementsByTag("p");
            String dealerName = pList.get(1).text();
            String address = pList.get(2).text().replaceAll("地址：", "");
            String phone = pList.get(3).text().replaceAll("客服电话：", "");
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else {
                if(!address.equals("")){
                    carDealer.setCityName(address.substring(0,2));
                }
            }
            carDealer.setDealerPhone(phone);
            carDealer.setDealerAddress(address);
            carDealer.setDealerName(dealerName);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //锦鸿集团
    @Test
    void testGetJinhongjituan() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="锦鸿集团经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("东风本田","http://www.jhqcjt.com/news/8/");
        brandMap.put("广汽本田","http://www.jhqcjt.com/news/12/");
        brandMap.put("广汽丰田","http://www.jhqcjt.com/news/10/");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass("spbq p_articles{");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByTag("h2").first().text();
                System.out.println(dealerName);
                String dealerUrl="http://www.jhqcjt.com"+dealerElement
                        .getElementsByTag("a").first().attr("href");
                Response rsp1 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String dealerHtml = rsp1.body().string();
                Document dealerDocument = Jsoup.parse(dealerHtml);
                Elements pList = dealerDocument.getElementsByClass("reset_style")
                        .get(1).getElementsByTag("p");
                String phone = pList.get(1).text().replaceAll("服务专线：", "");
                String address = pList.get(2).text().replaceAll("地址：", "");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerPhone(phone);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                carDealer.setDealerAddress(address);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //申湘汽车
    @Test
    void testGetShenxiangqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="申湘汽车经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("凯迪拉克","http://www.hnsxqc.com/Product/?6-1.html");
        brandMap.put("通用别克","http://www.hnsxqc.com/Product/?19-1.html");
        brandMap.put("雪佛兰","http://www.hnsxqc.com/Product/?20-1.html");
        brandMap.put("上汽大众","http://www.hnsxqc.com/Product/?21-1.html");
        brandMap.put("一汽丰田","http://www.hnsxqc.com/Product/?22-1.html");
        brandMap.put("上汽荣威","http://www.hnsxqc.com/Product/?23-1.html");
        brandMap.put("斯柯达","http://www.hnsxqc.com/Product/?24-1.html");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            byte[] bytes = response.body().bytes();
            String pageHtml = new String(bytes,"gbk");
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass(" fadeInUp wow");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByTag("h3").first().text();
                System.out.println(dealerName);
                Elements pList = dealerElement.getElementsByTag("p");
                String phone = pList.get(0).getElementsByTag("a").first().text();
                String address = pList.get(pList.size() - 1).text().replaceAll("公司地址：", "");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerPhone(phone);
                carDealer.setDealerName(dealerName);
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //建银华盛
    @Test
    void testGetjianyinhuasheng() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="建银华盛经销商数据.xlsx";
        String indexUrl="http://www.jyhsjt.com/index.php/index-carmap";
        Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("bus_list");
        for (Element dealerElement : dealerList) {
            CarDealer carDealer=new CarDealer();
            String dealerName = dealerElement.getElementsByTag("h3").first().text();
            String []messageArray=dealerElement.getElementsByClass("fr").first()
                    .getElementsByTag("p").first().text().split(" ");
            String brandName=messageArray[0].replaceAll("品牌：","");
            String phone=messageArray[1].replaceAll("电话：","");
            String address=messageArray[2].replaceAll("地址：","");
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                carDealer.setCityName(address.substring(0,2));
            }
            carDealer.setDealerPhone(phone);
            carDealer.setDealerName(dealerName);
            carDealer.setBrandName(brandName);
            carDealer.setDealerAddress(address);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //江苏海鹏
    @Test
    void testGetJiangsuhaipeng() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="江苏海鹏经销商数据.xlsx";
        String indexUrl_1="http://www.jshaipeng.net/pinpai.php?/20.html";
        String indexUrl_2="http://www.jshaipeng.net/pinpai.php?/19.html";
        List<String>brandList=new ArrayList<>();
        brandList.add("凯迪拉克");
        brandList.add("别克");
        brandList.add("雪佛兰");
        brandList.add("上汽大通");
        brandList.add("一汽-大众");
        brandList.add("北京现代");
        brandList.add("东风日产");
        brandList.add("领克");
        brandList.add("奔驰");
        brandList.add("吉利");
        //江阴
        Response response = OkHttpTools.getResponse(indexUrl_1, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = new String(response.body().bytes(),"gbk");
        Document document = Jsoup.parse(pageHtml);
        Elements news = document.getElementsByClass("news");
        for(int i=1;i<news.size();i++){
            String city="江阴市";
            CarDealer carDealer=new CarDealer();
            Element element = news.get(i);
            String dealerUrl="http://www.jshaipeng.net/"+element.getElementsByClass("tittle").first()
                    .getElementsByTag("a").first().attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerHtml = new String(rsp1.body().bytes(),"gbk");
            Document dealerDocument = Jsoup.parse(dealerHtml);
            Elements pList = dealerDocument.getElementsByClass("nr_show").first().getElementsByTag("p");
            String dealerName = pList.get(1).text();
            String address = pList.get(2).text().replaceAll("地址：","");
            String phone = pList.get(3).text().replaceAll("销售热线：", "");
            for (String brandName : brandList) {
                if(dealerName.indexOf(brandName)!=-1){
                    carDealer.setBrandName(brandName);
                    break;
                }
            }
            carDealer.setDealerPhone(phone);
            carDealer.setDealerAddress(address);
            carDealer.setDealerName(dealerName);
            carDealer.setCityName(city);
            resultList.add(carDealer);
        }
        //泰州
        Response rsp2 = OkHttpTools.getResponse(indexUrl_2, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml1 = new String(rsp2.body().bytes(),"gbk");
        Document document1 = Jsoup.parse(pageHtml1);
        Elements news1 = document1.getElementsByClass("news");
        for(int i=1;i<news1.size();i++){
            String city="泰州市";
            CarDealer carDealer=new CarDealer();
            Element element = news1.get(i);
            String dealerUrl="http://www.jshaipeng.net/"+element.getElementsByClass("tittle").first()
                    .getElementsByTag("a").first().attr("href");
            Response rsp1 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String dealerHtml = new String(rsp1.body().bytes(),"gbk");
            Document dealerDocument = Jsoup.parse(dealerHtml);
            Elements pList = dealerDocument.getElementsByClass("nr_show").first().getElementsByTag("p");
            String dealerName = pList.get(1).text();
            String address = pList.get(2).text().replaceAll("地址：","");
            String phone = pList.get(3).text().replaceAll("销售热线：", "");
            for (String brandName : brandList) {
                if(dealerName.indexOf(brandName)!=-1||address.indexOf(brandName)!=-1){
                    carDealer.setBrandName(brandName);
                    break;
                }
            }
            carDealer.setDealerPhone(phone);
            carDealer.setDealerAddress(address);
            carDealer.setDealerName(dealerName);
            carDealer.setCityName(city);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //辽宁惠华
    @Test
    void testGetLiaoninghuihua() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="辽宁惠华经销商数据.xlsx";
        List<String>indexUrlList=new ArrayList<>();
        indexUrlList.add("https://www.huihuacar.com/qixia/page/1");
        indexUrlList.add("https://www.huihuacar.com/qixia/page/2");
        indexUrlList.add("https://www.huihuacar.com/qixia/page/3");
        List<String>brandList=new ArrayList<>();
        brandList.add("大众");
        brandList.add("奥迪");
        brandList.add("丰田");
        brandList.add("奔腾");
        brandList.add("解放");
        brandList.add("轻卡");
        for (String indexUrl : indexUrlList) {
            Response response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass("qixialiste");
            for (Element dealerElement : dealerList) {
                try {
                    CarDealer carDealer=new CarDealer();
                    String regex="公司地址：|&nbsp;|电话：|手机/微信;|手机/微信：";
                    String dealerName = dealerElement.getElementsByClass("qixialisten2").first().text();
                    String dealerUrl=dealerElement.getElementsByTag("a").first().attr("href");
                    Response rsp1 = OkHttpTools.getResponse(dealerUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                    Document dealerDocument = Jsoup.parse(rsp1.body().string());
                    Elements pList = dealerDocument.getElementsByClass("qixia7").first().getElementsByTag("p");
                    String address = pList.get(0).text().replaceAll(regex, "").trim();
                    String phone = pList.get(1).text().replaceAll(regex, "").trim();
                    for (String brandName : brandList) {
                        if(dealerName.indexOf(brandName)!=-1){
                            carDealer.setBrandName(brandName);
                            break;
                        }
                    }
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        carDealer.setCityName(address.substring(0,2));
                    }
                    carDealer.setDealerPhone(phone);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerName(dealerName);
                    resultList.add(carDealer);
                }catch (Exception e){
                    continue;
                }

            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //江苏毅昌
    @Test
    void testGetJiangsuyichang() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="江苏毅昌经销商数据.xlsx";
        List<String>indexUrlList=new ArrayList<>();
        indexUrlList.add("http://www.ycjt.net.cn/jxscx.html#c_companyplace_network-15366358541307008-1");
        indexUrlList.add("http://www.ycjt.net.cn/jxscx.html#c_companyplace_network-15366358541307008-2");
        indexUrlList.add("http://www.ycjt.net.cn/jxscx.html#c_companyplace_network-15366358541307008-3");
        for (String indexUrl : indexUrlList) {
            driver.get(indexUrl);
            Document document = DriverTools.parseCurrentWebPage(15, driver);
            Elements dealerList = document.getElementsByClass("e_box e_box-000 p_MapList");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByTag("h3").first().text();
                String address = dealerElement.getElementsByClass("e_box e_box-000 p_AddressBox ")
                        .first().text().replaceAll("地址:","");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerName(dealerName);
                carDealer.setDealerAddress(address);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);
    }

    //明都汽车
    @Test
    void testGetMingduqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="明都汽车经销商数据.xlsx";
        String url="http://www.mdqm.com/dealernetwork.asp";
        Response response = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = new String(response.body().bytes(), Charset.forName("gbk"));
        Document document = Jsoup.parse(pageHtml);
        Elements dealerList = document.getElementsByClass("areaBox");
        for (Element dealerElement : dealerList) {
            String city = dealerElement.getElementsByClass("areaTitleb").first().text();
            Elements areaTitle = dealerElement.getElementsByClass("areaTitle");
            Elements areaContent = dealerElement.getElementsByClass("areaContent");
            for(int i=0;i<areaTitle.size();i++){
                try {
                    CarDealer carDealer=new CarDealer();
                    String[] areaTitleArray = areaTitle.get(i).text().split(" ");
                    System.out.println(areaTitle.get(i).text());
                    String brandName = areaTitleArray[0].replaceAll("品牌：", "");
                    String dealerName=areaTitleArray[1];
                    String[] areaContentArray = areaContent.get(i).text().split(" ");
                    String address=areaContentArray[0].replaceAll("4S店地址：","");
                    String phone = areaContentArray[1].replaceAll("销售热线：", "");
                    carDealer.setDealerPhone(phone);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerName(dealerName);
                    carDealer.setBrandName(brandName);
                    carDealer.setCityName(city);
                    resultList.add(carDealer);
                }catch (Exception e){
                    continue;
                }

            }
        }
        exportExcel(excelOutPutDir,companyName,resultList);

    }

    //万友汽车
    @Test
    void testGetWanyouqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="万友汽车经销商数据.xlsx";
        String url="http://www.wanyouauto.com.cn/wangluo/list-14.html";
        Response response = OkHttpTools.getResponse(url, Constant.DEFAULT_HEADERS_BUILDER, client);
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Element script = document.getElementsByTag("script").get(5);
        String regex="var data =|var serviceAddress =  \"http://wl.beshun.com.cn/tpl/beshun/images\";";
        String trim = script.html().replaceAll(regex, "").trim();
        String jsonString=trim.substring(0,trim.length()-1).replaceAll("\\\\","");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray provinceList = jsonObject.getJSONArray("China").getJSONObject(0).getJSONArray("cityList");
        for(int i=0;i<provinceList.size();i++){
            JSONObject provinceObj = provinceList.getJSONObject(i);
            JSONArray area = provinceObj.getJSONArray("area");
            for(int j=0;j<area.size();j++){
                JSONObject areaObj = area.getJSONObject(j);
                String brandName = areaObj.getString("areaName");
                JSONArray dealerList = areaObj.getJSONArray("shop");
                for(int k=0;k<dealerList.size();k++){
                    CarDealer carDealer=new CarDealer();
                    JSONObject dealerObj = dealerList.getJSONObject(k);
                    String address = dealerObj.getString("counterChineseAddress");
                    String phone = dealerObj.getString("tel");
                    String dealerName = dealerObj.getString("counterName");
                    String cityRegex="^(.*(市))";
                    Pattern pattern=Pattern.compile(cityRegex);
                    Matcher matcher = pattern.matcher(address);
                    if(matcher.find()){
                        carDealer.setCityName(matcher.group(0));
                    }else{
                        carDealer.setCityName(address.substring(0,2));
                    }
                    carDealer.setBrandName(brandName);
                    carDealer.setDealerPhone(phone);
                    carDealer.setDealerAddress(address);
                    carDealer.setDealerName(dealerName);
                    resultList.add(carDealer);
                }
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //有道汽车
    @Test
    void testGetYoudaoqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="有道汽车经销商数据.xlsx";
        Map<String,String>cityCodeMap=new HashMap<>();
        cityCodeMap.put("广州","0101");
        cityCodeMap.put("深圳","0102");
        cityCodeMap.put("佛山","0103");
        cityCodeMap.put("汕头","0104");
        cityCodeMap.put("清远","0105");
        cityCodeMap.put("从化","0106");
        cityCodeMap.put("湛江","0107");
        cityCodeMap.put("江门","0108");
        cityCodeMap.put("潮州","0109");
        cityCodeMap.put("中山","0110");
        cityCodeMap.put("南宁","0201");
        cityCodeMap.put("桂林","0202");
        cityCodeMap.put("玉林","0203");
        cityCodeMap.put("贵港","0204");
        cityCodeMap.put("柳州","0205");
        cityCodeMap.put("长沙","0301");
        cityCodeMap.put("株洲","0302");
        cityCodeMap.put("郴州","0303");
        String indexUrl="http://www.ydauto.cn/ajax/infor3.aspx";
        for(Map.Entry<String,String>cityCode:cityCodeMap.entrySet()){
            String city = cityCode.getKey();
            String code = cityCode.getValue();
            Map<String,String>paramMap=new HashMap<>();
            paramMap.put("code",code);
            Response response = OkHttpTools.postResponseFromForm(indexUrl, paramMap, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements liList = document.getElementsByTag("li");
            for (Element li : liList) {
                CarDealer carDealer=new CarDealer();
                String dealerName=li.text();
                String dealerCode = li.attr("onclick").replaceAll("prolist\\(|\\)", "");
                String dealerUrl="http://www.ydauto.cn/ajax/infor.aspx";
                Map<String,String>dealerParamMap=new HashMap<>();
                dealerParamMap.put("pid",dealerCode);
                Response rsp1 = OkHttpTools.postResponseFromForm(dealerUrl, dealerParamMap, Constant.DEFAULT_HEADERS_BUILDER, client);
                Document dealerDocument = Jsoup.parse(rsp1.body().string());
                Elements messageContent = dealerDocument.getElementsByClass("com_cont");
                String brandName = messageContent.get(0).getElementsByClass("left2 left").first().text();
                String address = messageContent.get(1).getElementsByClass("left2 left").first().text();
                String phone = messageContent.get(2).getElementsByClass("left2 left").first().text();
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                carDealer.setCityName(city);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //运通国融
    @Test
    void testGetYuntongguorong() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="运通国融经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("东风本田","http://www.wintopgroup.com.cn/cn/brand/pinpai/89.html");
        brandMap.put("捷豹路虎","http://www.wintopgroup.com.cn/cn/brand/pinpai/91.html");
        brandMap.put("别克","http://www.wintopgroup.com.cn/cn/brand/pinpai/92.html");
        brandMap.put("劳斯莱斯","http://www.wintopgroup.com.cn/cn/brand/pinpai/1106.html");
        brandMap.put("阿斯顿马丁","http://www.wintopgroup.com.cn/cn/brand/pinpai/1109.html");
        brandMap.put("兰博基尼","http://www.wintopgroup.com.cn/cn/brand/pinpai/1110.html");
        brandMap.put("奔驰","http://www.wintopgroup.com.cn/cn/brand/pinpai/1412.html");
        brandMap.put("林肯","http://www.wintopgroup.com.cn/cn/brand/pinpai/2361.html");
        brandMap.put("英菲尼迪","http://www.wintopgroup.com.cn/cn/brand/pinpai/81.html");
        brandMap.put("一汽大众","http://www.wintopgroup.com.cn/cn/brand/pinpai/83.html");
        brandMap.put("宝马","http://www.wintopgroup.com.cn/cn/brand/pinpai/85.html");
        brandMap.put("奥迪","http://www.wintopgroup.com.cn/cn/brand/pinpai/86.html");
        brandMap.put("宾利","http://www.wintopgroup.com.cn/cn/brand/pinpai/87.html");
        brandMap.put("一汽丰田","http://www.wintopgroup.com.cn/cn/brand/pinpai/88.html");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName=brand.getKey();
            String brandUrl = brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass("block-tab  current").first().getElementsByTag("li");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByClass("bc-1").first().text();
                String address = dealerElement.getElementsByClass("bc-2").first()
                        .text().replaceAll("地址1：", "");
                String phone =dealerElement.getElementsByClass("bc-3").first()
                        .text().replaceAll("电话：","");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //欧龙汽车
    @Test
    void testGetOulongqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="欧龙汽车经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("迈巴赫","http://www.oulongjituan.com/products_maybach.asp?SortId=249");
        brandMap.put("奔驰","http://www.oulongjituan.com/products_mercedes.asp?SortId=235");
        brandMap.put("捷豹路虎","http://www.oulongjituan.com/products_Jaguar.asp?SortId=236");
        brandMap.put("林肯","http://www.oulongjituan.com/products.asp?SortId=297");
        brandMap.put("英菲尼迪","http://www.oulongjituan.com/products_Infiniti.asp?SortId=240");
        brandMap.put("红旗","http://www.oulongjituan.com/products.asp?SortId=292");
        brandMap.put("一汽大众","http://www.oulongjituan.com/products_public.asp?SortId=207");
        brandMap.put("捷达","http://www.oulongjituan.com/products.asp?SortId=284");
        brandMap.put("广汽丰田","http://www.oulongjituan.com/products_toyota.asp?SortId=242");
        brandMap.put("广汽本田","http://www.oulongjituan.com/products_gqhonda.asp?SortId=289");
        brandMap.put("东风本田","http://www.oulongjituan.com/products_honda.asp?SortId=245");
        brandMap.put("长安福特","http://www.oulongjituan.com/products_ford.asp?SortId=208");
        brandMap.put("江铃福特","http://www.oulongjituan.com/products.asp?SortId=293");
        brandMap.put("雪佛兰","http://www.oulongjituan.com/products_Chevrolet.asp?SortId=229");
        brandMap.put("WEY","http://www.oulongjituan.com/products.asp?SortId=296");
        brandMap.put("领克","http://www.oulongjituan.com/products.asp?SortId=286");
        brandMap.put("长安","http://www.oulongjituan.com/products.asp?SortId=294");
        brandMap.put("长城","http://www.oulongjituan.com/products.asp?SortId=295");
        brandMap.put("比亚迪","http://www.oulongjituan.com/products_byd.asp?SortId=243");
        brandMap.put("奇瑞","http://www.oulongjituan.com/products_chery.asp?SortId=244");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass("prodLilist");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByTag("h2").first().text();
                Elements pList = dealerElement.getElementsByTag("p");
                String address = pList.get(0).text().replaceAll("地址:", "").trim();
                String phone = pList.get(1).text().replaceAll("销售热线：", "").trim();
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //远方汽车
    @Test
    void testGetYuanfangqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="远方汽车经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("宝马","http://www.yuanfangauto.com/ch/4sShow.asp?id=129," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=119," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=111");
        brandMap.put("东风日产","http://www.yuanfangauto.com/ch/4sShow.asp?id=121," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=107," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=112," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=113," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=123," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=127," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=130," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=133," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=132");
        brandMap.put("标致","http://www.yuanfangauto.com/ch/4sShow.asp?id=134,http://www.yuanfangauto.com/ch/4sShow.asp?id=135");
        brandMap.put("英菲尼迪","http://www.yuanfangauto.com/ch/4sShow.asp?id=140");
        brandMap.put("保时捷","http://www.yuanfangauto.com/ch/4sShow.asp?id=122");
        brandMap.put("奥迪","http://www.yuanfangauto.com/ch/4sShow.asp?id=106,http://www.yuanfangauto.com/ch/4sShow.asp?id=108");
        brandMap.put("北京现代","http://www.yuanfangauto.com/ch/4sShow.asp?id=124," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=126," +
                "http://www.yuanfangauto.com/ch/4sShow.asp?id=109");
        brandMap.put("一汽丰田","http://www.yuanfangauto.com/ch/4sShow.asp?id=110");
        brandMap.put("东风汽车","http://www.yuanfangauto.com/ch/4sShow.asp?id=114");
        brandMap.put("雪铁龙","http://www.yuanfangauto.com/ch/4sShow.asp?id=116");
        brandMap.put("一汽大众","http://www.yuanfangauto.com/ch/4sShow.asp?id=120");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getKey();
            String[] brandUrlList = brand.getValue().split(",");
            for (String brandUrl : brandUrlList) {
                CarDealer carDealer=new CarDealer();
                Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                String pageHtml = response.body().string();
                Document document = Jsoup.parse(pageHtml);
                Elements pList = document.getElementsByTag("tbody").first().getElementsByTag("p");
                String dealerName = pList.get(0).text();
                String phone = pList.get(1).text().replaceAll("销售热线：", "");
                String address = pList.get(3).text().replaceAll("地址：", "");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerAddress(address);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerName(dealerName);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //保定轩宇俊鹏
    @Test
    void testGetBaodingxuanyu() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="保定轩宇俊鹏经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("保时捷","http://www.xuanyugroup.com/brand/detail?id=1001");
        brandMap.put("奔驰","http://www.xuanyugroup.com/brand/detail?id=1003");
        brandMap.put("宝马","http://www.xuanyugroup.com/brand/detail?id=1004");
        brandMap.put("奥迪","http://www.xuanyugroup.com/brand/detail?id=1005");
        brandMap.put("捷豹","http://www.xuanyugroup.com/brand/detail?id=1006");
        brandMap.put("路虎","http://www.xuanyugroup.com/brand/detail?id=1007");
        brandMap.put("林肯","http://www.xuanyugroup.com/brand/detail?id=1009");
        brandMap.put("雷克萨斯","http://www.xuanyugroup.com/brand/detail?id=1010");
        brandMap.put("凯迪拉克","http://www.xuanyugroup.com/brand/detail?id=1011");
        brandMap.put("沃尔沃","http://www.xuanyugroup.com/brand/detail?id=1013");
        brandMap.put("MINI","http://www.xuanyugroup.com/brand/detail?id=1015");
        brandMap.put("大众","http://www.xuanyugroup.com/brand/detail?id=1017");
        brandMap.put("别克","http://www.xuanyugroup.com/brand/detail?id=1018");
        brandMap.put("雪佛兰","http://www.xuanyugroup.com/brand/detail?id=1019");
        brandMap.put("福特","http://www.xuanyugroup.com/brand/detail?id=1020");
        brandMap.put("一汽丰田","http://www.xuanyugroup.com/brand/detail?id=1021");
        brandMap.put("广汽丰田","http://www.xuanyugroup.com/brand/detail?id=1022");
        brandMap.put("东风本田","http://www.xuanyugroup.com/brand/detail?id=1023");
        brandMap.put("广汽本田","http://www.xuanyugroup.com/brand/detail?id=1024");
        brandMap.put("东风日产","http://www.xuanyugroup.com/brand/detail?id=1026");
        brandMap.put("Jeep","http://www.xuanyugroup.com/brand/detail?id=1028");
        brandMap.put("克莱斯勒","http://www.xuanyugroup.com/brand/detail?id=1029");
        brandMap.put("道奇","http://www.xuanyugroup.com/brand/detail?id=1031");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName=brand.getKey();
            String brandUrl = brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            String pageHtml = response.body().string();
            Document document = Jsoup.parse(pageHtml);
            Elements dealerList = document.getElementsByClass("company-item");
            for (Element dealerElement : dealerList) {
                CarDealer carDealer=new CarDealer();
                String dealerName = dealerElement.getElementsByClass("company-name").first().text();
                String address = dealerElement.getElementsByClass("company-address").first().text();
                String phone=dealerElement.getElementsByClass("bottom-left").first()
                        .getElementsByTag("p").first()
                        .text().replaceAll("销售热线：","");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerPhone(phone);
                carDealer.setDealerName(dealerName);
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //百事达汽车
    @Test
    void testBaishidaqiche() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="百事达汽车经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("奥迪","http://www.bescar.com/col.jsp?id=146");
        brandMap.put("奔驰","http://www.bescar.com/col.jsp?id=147");
        brandMap.put("凯迪拉克","http://www.bescar.com/col.jsp?id=148");
        brandMap.put("大众进口","http://www.bescar.com/col.jsp?id=149");
        brandMap.put("上汽大众","http://www.bescar.com/col.jsp?id=150");
        brandMap.put("一汽大众","http://www.bescar.com/col.jsp?id=151");
        brandMap.put("一汽丰田","http://www.bescar.com/col.jsp?id=152");
        brandMap.put("广汽丰田","http://www.bescar.com/col.jsp?id=153");
        brandMap.put("上汽通用别克","http://www.bescar.com/col.jsp?id=154");
        brandMap.put("东风本田","http://www.bescar.com/col.jsp?id=155");
        brandMap.put("上汽大众斯柯达","http://www.bescar.com/col.jsp?id=156");
        brandMap.put("上汽通用雪佛兰","http://www.bescar.com/col.jsp?id=157");
        brandMap.put("综合门店","http://www.bescar.com/col.jsp?id=158");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            driver.get(brandUrl);
            Document document = DriverTools.parseCurrentWebPage(15, driver);
            Elements dealerList = document.getElementsByClass("web_col_content");
            for(int i=3;i<dealerList.size();i++){
                CarDealer carDealer=new CarDealer();
                Element dealerElement = dealerList.get(i);
                String messageContent=dealerElement.text().trim();
                if(messageContent.equals("")||messageContent.indexOf("版权所有")!=-1){
                    continue;
                }
                String[] messageArray = messageContent.split(" ");
                String dealerName=messageArray[0];
                String address=messageArray[1].replaceAll("联系地址：","");
                String phone=messageArray[3].replaceAll("销售电话：","");
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setBrandName(brandName);
                carDealer.setDealerAddress(address);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerName(dealerName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //东安控股
    @Test
    void testDongankonggu() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="东安控股经销商数据.xlsx";
        Map<String,String>autohomeBrandMap=new HashMap<>();
        autohomeBrandMap.put("宝马","https://dealer.autohome.com.cn/2022026/#pvareaid=2113375");
        autohomeBrandMap.put("上汽大众","https://dealer.autohome.com.cn/2025546/");
        autohomeBrandMap.put("一汽大众","https://dealer.autohome.com.cn/121991");
        autohomeBrandMap.put("广汽丰田","https://dealer.autohome.com.cn/2006722/");
        autohomeBrandMap.put("东风本田","https://dealer.autohome.com.cn/72986");
        autohomeBrandMap.put("广汽本田","https://dealer.autohome.com.cn/2071351/");
        autohomeBrandMap.put("哈弗","https://dealer.autohome.com.cn/2109150/");
        Map<String,String>yicheBrandMap=new HashMap<>();
        yicheBrandMap.put("宝马","https://dealer.yiche.com/100064191/index.html");
        yicheBrandMap.put("奥迪","https://dealer.yiche.com/100039750/index.html");
        yicheBrandMap.put("上汽大众","https://dealer.yiche.com/100066393/index.html");
        yicheBrandMap.put("东风本田","https://dealer.yiche.com/100132165/");
        yicheBrandMap.put("哈弗","https://dealer.yiche.com/100035125");
        yicheBrandMap.put("北京汽车","https://dealer.yiche.com/100023351/");
        yicheBrandMap.put("长安欧尚","https://dealer.yiche.com/100028321/index.html");
        for(Map.Entry<String,String>brand:autohomeBrandMap.entrySet()){
            CarDealer carDealer=new CarDealer();
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            driver.get(brandUrl);
            Document document = DriverTools.parseCurrentWebPage(15,driver);
            Element dealerElement = document.getElementsByClass("allagency-cont").first();
            String dealerName = dealerElement.getElementById("starLevel").text();
            String phone=dealerElement.getElementsByClass("phone").first().text().replaceAll("咨询电话：","");
            String address = dealerElement.getElementsByClass("address").first().attr("title");
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                carDealer.setCityName(address.substring(0,2));
            }
            carDealer.setBrandName(brandName);
            carDealer.setDealerPhone(phone);
            carDealer.setDealerName(dealerName);
            carDealer.setDealerAddress(address);
            resultList.add(carDealer);
        }
        for(Map.Entry<String,String>brand:yicheBrandMap.entrySet()){
            CarDealer carDealer=new CarDealer();
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            driver.get(brandUrl);
            Document document = DriverTools.parseCurrentWebPage(15,driver);
            Element dealerElement = document.getElementsByClass("jxs_info  index_card").first();
            String dealerName = dealerElement.getElementsByTag("h2").first().text();
            String phone = dealerElement.getElementsByClass("info_c").first().text();
            String address = dealerElement.getElementsByClass("ads").first().attr("title");
            String cityRegex="^(.*(市))";
            Pattern pattern=Pattern.compile(cityRegex);
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                carDealer.setCityName(matcher.group(0));
            }else{
                carDealer.setCityName(address.substring(0,2));
            }
            carDealer.setDealerAddress(address);
            carDealer.setDealerPhone(phone);
            carDealer.setDealerName(dealerName);
            carDealer.setBrandName(brandName);
            resultList.add(carDealer);
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //陕西汽贸
    @Test
    void testGetShanxiqimao() throws IOException {
        List<CarDealer>resultList=new ArrayList<>();
        String companyName="陕西汽贸经销商数据.xlsx";
        Map<String,String>brandMap=new HashMap<>();
        brandMap.put("上汽大众","http://www.sxsqm.com/jypp_xx.php?id=2");
        brandMap.put("长安福特","http://www.sxsqm.com/jypp_xx.php?id=3");
        brandMap.put("长安汽车","http://www.sxsqm.com/jypp_xx.php?id=1");
        brandMap.put("斯柯达","http://www.sxsqm.com/jypp_xx.php?id=16");
        brandMap.put("中国一汽","http://www.sxsqm.com/jypp_xx.php?id=17");
        brandMap.put("合创","http://www.sxsqm.com/jypp_xx.php?id=21");
        brandMap.put("长安欧尚","http://www.sxsqm.com/jypp_xx.php?id=22");
        brandMap.put("宝骏","http://www.sxsqm.com/jypp_xx.php?id=23");
        brandMap.put("金杯","http://www.sxsqm.com/jypp_xx.php?id=25");
        brandMap.put("五菱","http://www.sxsqm.com/jypp_xx.php?id=26");
        brandMap.put("福田汽车","http://www.sxsqm.com/jypp_xx.php?id=28");
        brandMap.put("领跑汽车","http://www.sxsqm.com/jypp_xx.php?id=29");
        brandMap.put("哈弗","http://www.sxsqm.com/jypp_xx.php?id=30");
        for(Map.Entry<String,String>brand:brandMap.entrySet()){
            String brandName = brand.getKey();
            String brandUrl = brand.getValue();
            Response response = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
            Document document = Jsoup.parse(response.body().string());
            Elements titleList = document.getElementsByClass("ny_jypp4_zhong");
            Elements messageContent = document.getElementsByClass("ny_jypp5_zuo");
            for(int i=0;i<titleList.size();i++){
                CarDealer carDealer=new CarDealer();
                String dealerName = titleList.get(i).text();
                String[] messageArray = messageContent.get(i).text().replaceAll("电话：|地址：", "").split(" ");
                String phone=messageArray[0];
                String address=messageArray[1];
                String cityRegex="^(.*(市))";
                Pattern pattern=Pattern.compile(cityRegex);
                Matcher matcher = pattern.matcher(address);
                if(matcher.find()){
                    carDealer.setCityName(matcher.group(0));
                }else{
                    carDealer.setCityName(address.substring(0,2));
                }
                carDealer.setDealerName(dealerName);
                carDealer.setDealerPhone(phone);
                carDealer.setDealerAddress(address);
                carDealer.setBrandName(brandName);
                resultList.add(carDealer);
            }
        }
        exportExcel(excelOutPutDirPart3,companyName,resultList);
    }

    //获取易车裸车价
    @Test
    void testGetYicheluoche() throws IOException {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        Date startDate = new Date(startTime);
        System.out.println("任务开始:"+format.format(startDate));

        //线程池2
        ExecutorService es2 = Executors.newFixedThreadPool(10);
        CompletionService<String> threadPool2 = new ExecutorCompletionService<String>(es2);
        //线程池3
        ExecutorService es3 = Executors.newFixedThreadPool(10);
        CompletionService<String> threadPool3 = new ExecutorCompletionService<String>(es3);
        //一次性获取全部城市id
        String cityUrl="https://luochejia.yiche.com/web_api/web_app/api/v1/city/get_area_list";
        Response cityResponse =null;
        while(true){
            try {
                cityResponse = OkHttpTools.getResponse(cityUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                break;
            }catch (IOException e){

            }
        }
        String jsonString = cityResponse.body().string();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
        Map<Integer,String>cityMap=new HashMap<>();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject cityObj = jsonArray.getJSONObject(i);
            Integer cityId = cityObj.getInteger("CityId");
            String cityName = cityObj.getString("CityName");
            cityMap.put(cityId,cityName);
        }
        System.out.println("全部城市抓取完毕");
        String indexUrl="https://car.yiche.com/xuanchegongju/?mid=9";
        Response response =null;
        while(true){
            try {
                response = OkHttpTools.getResponse(indexUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                break;
            }catch (IOException e){

            }
        }
        String pageHtml = response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements brandList = document.getElementsByClass("brand-list-item");
        for (Element brandElement : brandList) {
            String letter=brandElement.attr("data-index");
            System.out.println(letter);
            Elements brandItemList = brandElement.getElementsByClass("item-brand");
            for (Element brand : brandItemList) {
                String brandName = brand.attr("data-name");
                String brandUrl="https://car.yiche.com"+brand.getElementsByTag("a")
                        .first().attr("href");
                Response rsp1=null;
                while(true){
                    try {
                        rsp1 = OkHttpTools.getResponse(brandUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                        break;
                    }catch (IOException e){

                    }
                }
                String brandHtml = rsp1.body().string();
                Document brandDocument = Jsoup.parse(brandHtml);
                try {
                    Elements aList = brandDocument.getElementsByClass("link-list pg-item")
                            .first().getElementsByTag("a");
                    System.out.println("-"+brandName+"---"+aList.size());
                    for (Element aElement : aList) {
                        String nextPage="https://car.yiche.com"+aElement.attr("href");
                        Response rsp2 = null;
                        while(true){
                            try {
                                rsp2 = OkHttpTools.getResponse(nextPage, Constant.DEFAULT_HEADERS_BUILDER, client);
                                break;
                            }catch (IOException e){

                            }
                        }
                        String nextPageHtml = rsp2.body().string();
                        Document nextPageDocument = Jsoup.parse(nextPageHtml);
                        Elements carSerialList = nextPageDocument.getElementsByClass("search-result-list-item");
                        System.out.println(" --共有车系数量:"+carSerialList.size()+" "+nextPage);
                        //根据车系幅度来并发
                        for (Element serialElement : carSerialList) {
                            //线程提交任务
                            threadPool.submit(new Callable<String>() {
                                @Override
                                public String call() throws Exception {
                                    String serialUrl="https://luochejia.yiche.com"+serialElement.getElementsByTag("a")
                                            .first().attr("href")+"price/?year=0";
                                    String serialName=serialElement.getElementsByTag("a").first()
                                            .getElementsByClass("cx-name text-hover").first().text();
                                    Response rsp3 =null;
                                    while(true){
                                        try {
                                            rsp3 = OkHttpTools.getResponse(serialUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                                            break;
                                        }catch (IOException e){

                                        }
                                    }
                                    String serialHtml = rsp3.body().string();
                                    Document serialDocument = Jsoup.parse(serialHtml);
                                    Elements pageSizeElement = serialDocument.getElementsByClass("sizes pg-item");
                                    if(pageSizeElement!=null&&pageSizeElement.size()>0){
                                        //线程池2
                                        for(Map.Entry<Integer,String>city:cityMap.entrySet()){
                                            threadPool2.submit(new Callable<String>() {
                                                @Override
                                                public String call() throws Exception {
                                                    Integer cityId = city.getKey();
                                                    String cityName = city.getValue();
                                                    Integer pageSize=Integer.parseInt(pageSizeElement.first().getElementsByTag("span").first().text());
                                                    for(int i=1;i<=pageSize;i++){
                                                        String nextSerialPageUrl=serialUrl+"&page="+i+"&cityid="+cityId;
                                                        Response rsp4=null;
                                                        while(true){
                                                            try {
                                                                rsp4 = OkHttpTools.getResponse(nextSerialPageUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                                                                break;
                                                            }catch (IOException e){

                                                            }
                                                        }
                                                        String nextSerialHtml = rsp4.body().string();
                                                        Document nextSerialDocument = Jsoup.parse(nextSerialHtml);
                                                        Elements modelList = nextSerialDocument.getElementsByClass("lcjd-desc");
                                                        for (Element model : modelList) {
                                                            System.out.println("  ---"+cityName+"---"+letter+"---"+brandName+"---"+serialName+"---"+model.text());
                                                        }
                                                    }
                                                    return null;
                                                }
                                            });

                                        }
                                        for(Map.Entry<Integer,String>city:cityMap.entrySet()){
                                            threadPool2.take().get();
                                        }
                                    }else {
                                        //线程池3
                                        for(Map.Entry<Integer,String>city:cityMap.entrySet()){
                                            threadPool3.submit(new Callable<String>() {
                                                @Override
                                                public String call() throws Exception {
                                                    Integer cityId = city.getKey();
                                                    String cityName = city.getValue();
                                                    String nextCityUrl=serialUrl+"&cityid="+cityId;
                                                    Response rsp5=null;
                                                    while(true){
                                                        try {
                                                            rsp5 = OkHttpTools.getResponse(nextCityUrl, Constant.DEFAULT_HEADERS_BUILDER, client);
                                                            break;
                                                        }catch (IOException e){

                                                        }
                                                    }
                                                    String nextSerialHtml = rsp5.body().string();
                                                    Document nextSerialDocument = Jsoup.parse(nextSerialHtml);
                                                    Elements modelList = nextSerialDocument.getElementsByClass("lcjd-desc");
                                                    for (Element model : modelList) {
                                                        System.out.println("  ---"+cityName+"---"+letter+"---"+brandName+"---"+serialName+"---"+model.text());
                                                    }
                                                    return null;
                                                }
                                            });
                                        }
                                        for(Map.Entry<Integer,String>city:cityMap.entrySet()){
                                            threadPool3.take().get();
                                        }
                                    }
                                    return null;
                                }
                            });
                        }
                        //线程阻塞  线程池1
                        for (Element serialElement : carSerialList){
                            try {
                                String s = threadPool.take().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }catch (NullPointerException e){
                    continue;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        Date endDate = new Date(endTime);
        //总耗时（毫秒）
        long time=endTime-startTime;
        //秒
        Double secondTime=new Double(time/1000);
        //分
        String minTime = String.format("%.2f", (secondTime / 60));
        //时
        String hourTime = String.format("%.2f", (secondTime / 60 / 60));
        System.out.println("任务结束:"+format.format(endDate));
        System.out.println("全部数据抓取完毕！共耗时"+time+"ms,整合"+secondTime+"s,"+minTime+"min,"+hourTime+"hour");
    }

}
