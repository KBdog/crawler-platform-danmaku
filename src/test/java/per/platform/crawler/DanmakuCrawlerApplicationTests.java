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

    @Test
    void testGetAgeFans() throws InterruptedException {
        List<String>apiList=new ArrayList<>();
        apiList.add("/age/player/ckx1/\\?url=");
        apiList.add("/age/player/dpx/\\?url=");
        apiList.add("/age/player/dp2/\\?url=");
        String url="https://www.agefans.vip/play/20220062?playid=3_1";
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(15,TimeUnit.SECONDS);
        Thread.sleep(2000);
        Document document = DriverTools.parseCurrentWebPage(15, driver);
        String videoUrl=document.getElementById("age_playfram").attr("src");
        System.out.println(videoUrl);
        for (String api : apiList) {
            try {
                videoUrl=videoUrl.split(api)[1]
                        .replaceAll("%25","%")
                        .replaceAll("%5f","_")
                        .replaceAll("%5F","_")
                        .replaceAll("%2d","-")
                        .replaceAll("%2D","-")
                        .replaceAll("%3a",":")
                        .replaceAll("%3A",":")
                        .replaceAll("%2e",".")
                        .replaceAll("%2E",".")
                        .replaceAll("%2f","/")
                        .replaceAll("%2F","/")
                        .replaceAll("%3f","?")
                        .replaceAll("%3F","?")
                        .replaceAll("%3d","=")
                        .replaceAll("%3D","=")
                        .replaceAll("%26","&")
                        .split("&vlt_l=0&vlt_r=0")[0]
                        .split("&getplayer")[0];
                break;
            }catch (ArrayIndexOutOfBoundsException e){
                continue;
            }
        }
//        System.out.println(document.html());
        System.out.println(videoUrl);
        if(videoUrl.endsWith("m3u8")){
            System.out.println("该链接指向视频格式为m3u8");
        }
    }

    @Test
    void testParseVideo(){
        String url="https%3A%2F%2Fv3.bdxiguavod.com%2F06c3fb53daf6d42e489ce281225ab42b%2F61ddf736%2Fvideo%2Ftos%2Fcn%2Ftos%2Dcn%2Dv%2D3506%2Fd0a3c52da37445419a19b1718222f127%2F";
        url=url.replaceAll("%5f","_")
                .replaceAll("%5F","_")
                .replaceAll("%2d","-")
                .replaceAll("%2D","-")
                .replaceAll("%3a",":")
                .replaceAll("%3A",":")
                .replaceAll("%2e",".")
                .replaceAll("%2E",".")
                .replaceAll("%2f","/")
                .replaceAll("%2F","/")
                .replaceAll("%3f","?")
                .replaceAll("%3F","?")
                .replaceAll("%3d","=")
                .replaceAll("%3D","=")
                .replaceAll("%26","&")
                .split("&vlt_l=0&vlt_r=0")[0];
        System.out.println(url);
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



}
