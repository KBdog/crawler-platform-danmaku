package per.platform.crawler.model;

import lombok.Data;

/**
 * @author lipeiyu
 * @package per.platform.crawler.model
 * @description 汽车经销商实体
 * @date 2021/12/20 14:20
 */
@Data
public class CarDealer {
    //城市名
    private String cityName;
    //品牌名
    private String brandName;
    //经销商名
    private String dealerName;
    //经销商地址
    private String dealerAddress;
    //经销商电话
    private String dealerPhone;

    public CarDealer(){
        this.cityName="";
        this.brandName="";
        this.dealerName="";
        this.dealerAddress="";
        this.dealerPhone="";
    }
}
