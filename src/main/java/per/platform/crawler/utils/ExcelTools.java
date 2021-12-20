package per.platform.crawler.utils;

import per.platform.crawler.model.CarDealer;
import per.platform.crawler.model.Comment;
import per.platform.crawler.model.News;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lipeiyu
 * @package com.selenium.test.utils
 * @description 导出excel工具类
 * @date 2021/10/22 15:17
 */
public class ExcelTools {
    public static boolean exportExcelForNews(Map<String,String> newsMap, String[]titles, FileOutputStream fos){
        boolean flag=false;
        //1.先创建一个工作簿workbook,对应于一个excel文件
        XSSFWorkbook workbook=new XSSFWorkbook();
        //2.在工作簿中创建一个sheet,对应于excel中的sheet,并设置宽度
        XSSFSheet sheet=workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth(70);
        //3.在sheet中添加表头第0行
        XSSFRow row=sheet.createRow(0);
        //4.创建单元格样式
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //居中样式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //5.设置表头,添加列标题
        XSSFCell cell=null;
        for(int i=0;i<titles.length;i++){
            //创建表头单元格
            cell=row.createCell(i);
            //设置列名
            cell.setCellValue(titles[i]);
            //设置列样式
            cell.setCellStyle(cellStyle);
        }
        int i=1;
        for(Map.Entry<String,String>tmp:newsMap.entrySet()){
            String title = tmp.getKey();
            String url = tmp.getValue();
            //逐行添加数据
            row=sheet.createRow(i);
            row.createCell(0).setCellValue(title);
            row.createCell(1).setCellValue(url);
            i++;
        }
        //8.将文件写出客户端
        try {
            workbook.write(fos);
            fos.flush();
            flag=true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return flag;
        }
    }

    public static boolean exportExcelForNewsV2(List<News>newsList, String[]titles, FileOutputStream fos){
        boolean flag=false;
        //1.先创建一个工作簿workbook,对应于一个excel文件
        XSSFWorkbook workbook=new XSSFWorkbook();
        //2.在工作簿中创建一个sheet,对应于excel中的sheet,并设置宽度
        XSSFSheet sheet=workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth(70);
        //3.在sheet中添加表头第0行
        XSSFRow row=sheet.createRow(0);
        //4.创建单元格样式
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //居中样式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //5.设置表头,添加列标题
        XSSFCell cell=null;
        for(int i=0;i<titles.length;i++){
            //创建表头单元格
            cell=row.createCell(i);
            //设置列名
            cell.setCellValue(titles[i]);
            //设置列样式
            cell.setCellStyle(cellStyle);
        }
        int i=1;
        for (News news : newsList) {
            String publishTime=news.getPublishDate();
            String title=news.getNewsTitle();
            String link=news.getNewsLink();
            row=sheet.createRow(i);
            row.createCell(0).setCellValue(publishTime);
            row.createCell(1).setCellValue(title);
            row.createCell(2).setCellValue(link);
            i++;
        }
        //8.将文件写出客户端
        try {
            workbook.write(fos);
            fos.flush();
            flag=true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return flag;
        }
    }

    public static boolean exportExcelForDouyinV3(List<Comment>commentList, String[]titles, FileOutputStream fos){
        boolean flag=false;
        //1.先创建一个工作簿workbook,对应于一个excel文件
        XSSFWorkbook workbook=new XSSFWorkbook();
        //2.在工作簿中创建一个sheet,对应于excel中的sheet,并设置宽度
        XSSFSheet sheet=workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth(50);
        //3.在sheet中添加表头第0行
        XSSFRow row=sheet.createRow(0);
        //4.创建单元格样式
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //居中样式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //背景
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        //边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //模式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //5.设置表头,添加列标题
        XSSFCell cell=null;
        for(int i=0;i<titles.length;i++){
            //创建表头单元格
            cell=row.createCell(i);
            //设置列名
            cell.setCellValue(titles[i]);
            //设置列样式
            cell.setCellStyle(cellStyle);
        }
        int i=1;
        for (Comment comment : commentList) {
            String publishTime=comment.getCommentTime();
            String userName=comment.getUserName();
            String userComment=comment.getUserComment();
            row=sheet.createRow(i);
            row.createCell(0).setCellValue(publishTime);
            row.createCell(1).setCellValue(userName);
            row.createCell(2).setCellValue(userComment);
            i++;
        }
        //8.将文件写出客户端
        try {
            workbook.write(fos);
            fos.flush();
            flag=true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return flag;
        }
    }

    public static boolean exportExcelForDouyinV4(List<CarDealer>dealerList, String[]titles, FileOutputStream fos){
        boolean flag=false;
        //1.先创建一个工作簿workbook,对应于一个excel文件
        XSSFWorkbook workbook=new XSSFWorkbook();
        //2.在工作簿中创建一个sheet,对应于excel中的sheet,并设置宽度
        XSSFSheet sheet=workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth(50);
        //3.在sheet中添加表头第0行
        XSSFRow row=sheet.createRow(0);
        //4.创建单元格样式
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //居中样式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //背景
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        //边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //模式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //5.设置表头,添加列标题
        XSSFCell cell=null;
        for(int i=0;i<titles.length;i++){
            //创建表头单元格
            cell=row.createCell(i);
            //设置列名
            cell.setCellValue(titles[i]);
            //设置列样式
            cell.setCellStyle(cellStyle);
        }
        int i=1;
        for (CarDealer dealer : dealerList) {
            row=sheet.createRow(i);
            row.createCell(0).setCellValue(dealer.getCityName());
            row.createCell(1).setCellValue(dealer.getBrandName());
            row.createCell(2).setCellValue(dealer.getDealerName());
            row.createCell(3).setCellValue(dealer.getDealerAddress());
            row.createCell(4).setCellValue(dealer.getDealerPhone());
            i++;
        }
        //8.将文件写出客户端
        try {
            workbook.write(fos);
            fos.flush();
            flag=true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return flag;
        }
    }

}
