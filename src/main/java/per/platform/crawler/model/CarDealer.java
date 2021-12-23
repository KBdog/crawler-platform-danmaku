package per.platform.crawler.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author kbdog
 * @package per.platform.crawler.model
 * @description 汽车经销商实体
 * @date 2021/12/20 14:20
 */
@Data
public class CarDealer {
    //城市名
    @JSONField
    private String cityName;
    //品牌名
    @JSONField(ordinal = 1)
    private String brandName;
    //经销商名
    @JSONField(ordinal = 2)
    private String dealerName;
    //经销商地址
    @JSONField(ordinal = 3)
    private String dealerAddress;
    //经销商电话
    @JSONField(ordinal = 4)
    private String dealerPhone;

    public CarDealer(){
        this.cityName="";
        this.brandName="";
        this.dealerName="";
        this.dealerAddress="";
        this.dealerPhone="";
    }
}
