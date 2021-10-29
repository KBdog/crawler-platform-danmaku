package per.platform.crawler.model;

import lombok.Data;

/**
 * @author lipeiyu
 * @package com.selenium.test.model
 * @description 弹幕评论
 * @date 2021/10/25 15:02
 */
@Data
public class Comment {
    private String commentTime;
    private String userName;
    private String userComment;
}
