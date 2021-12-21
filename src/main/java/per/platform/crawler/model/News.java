package per.platform.crawler.model;

import lombok.Data;

/**
 * @author kbdog
 * @package per.platform.crawler.model
 * @description 新闻实体类
 * @date 2021/10/25 10:00
 */
@Data
public class News {
    private String publishDate;
    private String newsTitle;
    private String newsLink;
}
