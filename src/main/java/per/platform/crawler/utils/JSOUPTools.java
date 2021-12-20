package per.platform.crawler.utils;

import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import per.platform.crawler.constant.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author kbdog
 * @package per.kbwebstack.crawler.api.utils
 * @description jsoup解析工具类
 * @date 2021/8/10 22:32
 */
public class JSOUPTools {
    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).headers(Constant.DEFAULT_HEADERS).proxy(new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("127.0.0.1",1080))).get();
    }
    public static Element getElementByClass(String url,String className) throws IOException {
        Document doc = Jsoup.connect(url).headers(Constant.DEFAULT_HEADERS).proxy(new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("127.0.0.1",1080))).get();
        return doc.getElementsByClass(className).first();
    }
    public static Elements getElementsByClass(String url,String className) throws IOException {
        Document doc = Jsoup.connect(url).headers(Constant.DEFAULT_HEADERS).proxy(new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("127.0.0.1",1080))).get();
        return doc.getElementsByClass(className);
    }
    public static Element getElementByClass(Response response, String className) throws IOException {
        String pageHtml=response.body().string();
        Document document = Jsoup.parse(pageHtml);
        return document.getElementsByClass(className).first();
    }

    public static Elements getElementsByClass(Response response, String className) throws IOException {
        String pageHtml=response.body().string();
        Document document = Jsoup.parse(pageHtml);
        return document.getElementsByClass(className);
    }

    /**
     * 根据多个classname获取多个标签
     * @param response
     * @param classNameArray
     * @return
     * @throws IOException
     */
    public static Elements getMultiElementsByClasses(Response response, String...classNameArray) throws IOException {
        String pageHtml=response.body().string();
        Document document = Jsoup.parse(pageHtml);
        Elements result=new Elements();
        for (String className : classNameArray) {
            Elements elements = document.getElementsByClass(className);
            result.addAll(elements);
        }
        return result;
    }

}
