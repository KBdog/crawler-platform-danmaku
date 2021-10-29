package per.platform.crawler.controller;

import per.platform.crawler.model.Comment;
import per.platform.crawler.utils.DriverTools;
import per.platform.crawler.utils.ExcelTools;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lipeiyu
 * @package com.selenium.test.controller
 * @description 爬各大平台直播间弹幕接口
 * @date 2021/10/26 9:57
 */
@RequestMapping("/crawler/danmaku")
@RestController
@Slf4j
public class CrawlerDanmakuController {
    @Value("${crawler.time}")
    private Integer minutes;
    @Value("${crawler.export.filePath}")
    private String exportPath;
    @Autowired
    private WebDriver driver;
    @Autowired
    private SimpleDateFormat format;

    /**
     * 是否在进行爬虫的标记信号
     */
    private volatile boolean isCrawling=false;

    /**
     * 当前爬虫执行的平台 1:虎牙 2:b站 3:抖音 4:斗鱼
     */
    private volatile Integer curCrawler;

    /**
     * 修改标志位
     * @return
     */
    @GetMapping("/shutdown")
    public String shutDownCrawling(){
        isCrawling=false;
        if(isCrawling==false){
            return "爬虫标志位设值中断成功!";
        }else {
            return "爬虫标志位设值中断失败!";
        }
    }

    @GetMapping("/huya")
    public String getHuyaDanmaku(@RequestParam("url")String url){
        if(isCrawling==true){
            return "爬虫正在进行...";
        }
        curCrawler=1;
        isCrawling=true;
        FileOutputStream fos=null;
        //最后结果集
        List<Comment> resultList=new ArrayList<>();
        try {
            log.info("虎牙直播间地址:"+url);
            driver.get(url);
            log.info(url+"加载完毕");
            long startTimeMillis = System.currentTimeMillis();
            long endTimeMillis=startTimeMillis+(minutes*60*1000);
            String startTime = format.format(startTimeMillis);
            String endTime=format.format(endTimeMillis);
            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
            long currentTimeMillis=new Date().getTime();
            //记录重复发言
            List<Comment>repeatList=new ArrayList<>();
            while(currentTimeMillis<endTimeMillis){
                if(isCrawling==false){
                    return "爬虫终止!";
                }
                currentTimeMillis=new Date().getTime();
                Document document = DriverTools.parseCurrentWebPage(15, driver);
                Elements danmakuList = document.getElementsByClass("J_msg");
                for(int i=0;i<danmakuList.size();i++){
                    if(i==0){
                        continue;
                    }
                    try {
                        Element element = danmakuList.get(i);
                        Element commentContent = element.getElementsByTag("div").first();
                        String userName = commentContent.getElementsByClass("name J_userMenu")
                                .first().text();
                        String userComment = commentContent.getElementsByClass("msg")
                                .first().text();
                        if(userComment.equals("驾临直播间")){
                            continue;
                        }
                        Comment comment=new Comment();
                        comment.setCommentTime(format.format(currentTimeMillis));
                        comment.setUserName(userName);
                        comment.setUserComment(userComment);
                        boolean isRepeat=false;
                        //判断是不是重复评论
                        for (Comment tmp : repeatList) {
                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
                                isRepeat=true;
                                break;
                            }
                        }
                        if(isRepeat==false){
                            log.info(format.format(currentTimeMillis)+"--"+userName+"："+userComment);
                            repeatList.add(comment);
                            resultList.add(comment);
                        }
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            //其他进程在爬,则不导出报表了
            if(curCrawler!=1){
                return "谷歌驱动正在被其他爬虫线程在使用,请稍后重新发请求...";
            }
            if(isCrawling==false){
                log.info("爬虫终止!");
                return "爬虫终止!";
            }
            isCrawling=false;
            long currentTimeStamp = System.currentTimeMillis();
            File excelFile=new File(exportPath+"/虎牙弹幕_"+format.format(currentTimeStamp)+".xlsx");
            try {
                fos=new FileOutputStream(excelFile);
                String titles[]=new String[]{"评论时间","用户","评论内容"};
                boolean flag=false;
                if(resultList.size()>0){
                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
                }
                if(flag){
                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
                }else {
                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "爬虫任务已执行完毕!";
            }
        }
    }

    @GetMapping("/bilibili")
    public String getBilibiliDanmaku(@RequestParam("url")String url){
        if(isCrawling==true){
            return "爬虫正在进行...";
        }
        curCrawler=2;
        isCrawling=true;
        FileOutputStream fos=null;
        //最后结果集
        List<Comment> resultList=new ArrayList<>();
        try {
            log.info("b站直播间地址:"+url);
            driver.get(url);
            log.info(url+"加载完毕");
            long startTimeMillis = System.currentTimeMillis();
            long endTimeMillis=startTimeMillis+(minutes*60*1000);
            String startTime = format.format(startTimeMillis);
            String endTime=format.format(endTimeMillis);
            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
            long currentTimeMillis=new Date().getTime();
            //记录重复发言
            List<Comment>repeatList=new ArrayList<>();
            while(currentTimeMillis<endTimeMillis){
                if(isCrawling==false){
                    return "爬虫终止!";
                }
                currentTimeMillis=new Date().getTime();
                Document document = DriverTools.parseCurrentWebPage(15, driver);
                Elements danmakuList = document.getElementsByClass("chat-item danmaku-item ");
                Elements tmpDanmakuList = document.getElementsByClass("chat-item danmaku-item chat-colorful-bubble");
                danmakuList.addAll(tmpDanmakuList);
                for(int i=0;i<danmakuList.size();i++){
                    try {
                        Element element = danmakuList.get(i);
                        String userName = element.getElementsByClass("user-name v-middle pointer open-menu")
                                .first().text().replaceAll(":","").trim();
                        String userComment = element.getElementsByClass("danmaku-content v-middle pointer ts-dot-2 open-menu")
                                .first().text();
                        Comment comment=new Comment();
                        comment.setCommentTime(format.format(currentTimeMillis));
                        comment.setUserName(userName);
                        comment.setUserComment(userComment);
                        boolean isRepeat=false;
                        //判断是不是重复评论
                        for (Comment tmp : repeatList) {
                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
                                isRepeat=true;
                                break;
                            }
                        }
                        if(isRepeat==false){
                            log.info(format.format(currentTimeMillis)+"--"+userName+"："+userComment);
                            repeatList.add(comment);
                            resultList.add(comment);
                        }
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            //其他进程在爬,则不导出报表了
            if(curCrawler!=2){
                return "谷歌驱动正在被其他爬虫线程在使用,请稍后重新发请求...";
            }
            if(isCrawling==false){
                log.info("爬虫终止!");
                return "爬虫终止!";
            }
            isCrawling=false;
            long currentTimeStamp = System.currentTimeMillis();
            File excelFile=new File(exportPath+"/b站弹幕_"+format.format(currentTimeStamp)+".xlsx");
            try {
                fos=new FileOutputStream(excelFile);
                String titles[]=new String[]{"评论时间","用户","评论内容"};
                boolean flag=false;
                if(resultList.size()>0){
                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
                }
                if(flag){
                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
                }else {
                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "爬虫任务已执行完毕!";
            }
        }
    }

    @GetMapping("/douyin")
    public String getDouyinDanmaku(@RequestParam("url")String url){
        if(isCrawling==true){
            return "爬虫正在进行...";
        }
        curCrawler=3;
        isCrawling=true;
        FileOutputStream fos=null;
        List<Comment> resultList=new ArrayList<>();
        try {
            log.info("抖音直播间地址:"+url);
            driver.get(url);
            log.info(url+"加载完毕");
            long startTimeMillis = System.currentTimeMillis();
            long endTimeMillis=startTimeMillis+(minutes*60*1000);
            String startTime = format.format(startTimeMillis);
            String endTime=format.format(endTimeMillis);
            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
            long currentTimeMillis=new Date().getTime();
            //记录重复发言
            List<Comment>repeatList=new ArrayList<>();
            while(currentTimeMillis<endTimeMillis){
                if(isCrawling==false){
                    return "爬虫终止!";
                }
                currentTimeMillis=new Date().getTime();
                Document document = DriverTools.parseCurrentWebPage(15, driver);
                Elements danmakuList = document.getElementsByClass("webcast-chatroom___item");
                for(int i=0;i<danmakuList.size();i++){
                    if(i==0){
                        continue;
                    }
                    try {
                        Element element = danmakuList.get(i);
                        Element commentContent = element.getElementsByTag("div").first();
                        String userName = commentContent.getElementsByClass("_205YX559")
                                .first().text().replaceAll("：","");
                        String userComment = commentContent.getElementsByClass("_2Fj-jpg0")
                                .first().text();
                        if(userComment.equals("来了")){
                            continue;
                        }
                        Comment comment=new Comment();
                        comment.setCommentTime(format.format(currentTimeMillis));
                        comment.setUserName(userName);
                        comment.setUserComment(userComment);
                        boolean isRepeat=false;
                        //判断是不是重复评论
                        for (Comment tmp : repeatList) {
                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
                                isRepeat=true;
                                break;
                            }
                        }
                        if(isRepeat==false){
                            log.info(format.format(currentTimeMillis)+"--"+userName+"："+userComment);
                            repeatList.add(comment);
                            resultList.add(comment);
                        }
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            //其他进程在爬,则不导出报表了
            if(curCrawler!=3){
                return "谷歌驱动正在被其他爬虫线程在使用,请稍后重新发请求...";
            }
            if(isCrawling==false){
                log.info("爬虫终止!");
                return "爬虫终止!";
            }
            isCrawling=false;
            long currentTimeStamp = System.currentTimeMillis();
            File excelFile=new File(exportPath+"/抖音评论_"+format.format(currentTimeStamp)+".xlsx");
            try {
                fos=new FileOutputStream(excelFile);
                String titles[]=new String[]{"评论时间","用户","评论内容"};
                boolean flag=false;
                if(resultList.size()>0){
                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
                }
                if(flag){
                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
                }else {
                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "爬虫任务已执行完毕!";
            }
        }
    }

    @GetMapping("/douyu")
    public String getDouyuDanmaku(@RequestParam("url")String url){
        if(isCrawling==true){
            return "爬虫正在进行...";
        }
        curCrawler=4;
        isCrawling=true;
        FileOutputStream fos=null;
        List<Comment> resultList=new ArrayList<>();
        try {
            log.info("斗鱼直播间地址:"+url);
            driver.get(url);
            log.info(url+"加载完毕");
            long startTimeMillis = System.currentTimeMillis();
            long endTimeMillis=startTimeMillis+(minutes*60*1000);
            String startTime = format.format(startTimeMillis);
            String endTime=format.format(endTimeMillis);
            log.info("开始时间:"+startTime+"--结束时间:"+endTime);
            long currentTimeMillis=new Date().getTime();
            //记录重复发言
            List<Comment>repeatList=new ArrayList<>();
            while(currentTimeMillis<endTimeMillis){
                if(isCrawling==false){
                    return "爬虫终止!";
                }
                currentTimeMillis=new Date().getTime();
                Document document = DriverTools.parseCurrentWebPage(15, driver);
                Elements danmakuList = document.getElementsByClass("Barrage-listItem");
                for(int i=0;i<danmakuList.size();i++){
                    if(i==0){
                        continue;
                    }
                    try {
                        Element element = danmakuList.get(i);
                        Element commentContent = element.getElementsByTag("div").first();
                        String userName = commentContent.getElementsByClass("Barrage-nickName Barrage-nickName--blue js-nick")
                                .first().text().replaceAll("：","");
                        String userComment = commentContent.getElementsByClass("Barrage-content")
                                .first().text();
                        if(userComment.equals("欢迎来到本直播间")){
                            continue;
                        }
                        Comment comment=new Comment();
                        comment.setCommentTime(format.format(currentTimeMillis));
                        comment.setUserName(userName);
                        comment.setUserComment(userComment);
                        boolean isRepeat=false;
                        //判断是不是重复评论
                        for (Comment tmp : repeatList) {
                            if(tmp.getUserName().equals(userName)&&tmp.getUserComment().equals(userComment)){
                                isRepeat=true;
                                break;
                            }
                        }
                        if(isRepeat==false){
                            log.info(format.format(currentTimeMillis)+"--"+userName+"："+userComment);
                            repeatList.add(comment);
                            resultList.add(comment);
                        }
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            //其他进程在爬,则不导出报表了
            if(curCrawler!=4){
                return "谷歌驱动正在被其他爬虫线程在使用,请稍后重新发请求...";
            }
            if(isCrawling==false){
                log.info("爬虫终止!");
                return "爬虫终止!";
            }
            isCrawling=false;
            long currentTimeStamp = System.currentTimeMillis();
            File excelFile=new File(exportPath+"/斗鱼弹幕_"+format.format(currentTimeStamp)+".xlsx");
            try {
                fos=new FileOutputStream(excelFile);
                String titles[]=new String[]{"评论时间","用户","评论内容"};
                boolean flag=false;
                if(resultList.size()>0){
                    flag = ExcelTools.exportExcelForDouyinV3(resultList, titles, fos);
                }
                if(flag){
                    log.info("导出报表成功! "+excelFile.getAbsolutePath());
                }else {
                    log.info("导出报表失败! "+excelFile.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "爬虫任务已执行完毕!";
            }
        }
    }
}
