package per.platform.crawler.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * @author kbdog
 * @package per.kbwebstack.crawler.api.utils
 * @description json格式化工具类
 * @date 2021/8/16 15:36
 */
public class JSONTools {
    //格式化json
    public static String formatJSON(JSON json){
        return JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }
    //对集合里的属性按jsonfield顺序排序
    public static <T> JSONArray sortByField(List<T> collection){
        JSONArray jsonArray=new JSONArray();
        for (T t : collection) {
            JSONObject jsonObject= JSONObject.parseObject(JSON.toJSONString(t), Feature.OrderedField);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
    //对对象里的属性按jsonfield顺序排序
    public static <T> JSONObject sortByField(T t){
        JSONObject jsonObject= JSONObject.parseObject(JSON.toJSONString(t), Feature.OrderedField);
        return jsonObject;
    }
}
