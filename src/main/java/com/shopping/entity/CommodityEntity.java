package com.shopping.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CommodityEntity {
    private Integer id;

    private String name;
    //单图的图片链接
    private String url;

    private Double price;

    private Integer sales;

    private Double endPrice;

    private Double subtract;

    private Integer repertory;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GTM+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GTM+8")
    private Date updateTime;

    private String detailurl;

    private String specsurl;

    private String saleurl;

    private String subname;

    private Integer carNum;

    private String aid;//优惠类型

    private Integer num;//商品的数量

    private CommercialEntity commercial;

    private Integer integral;

    private Integer isintegral;

    private Integer beretail;//能够成为分销商吗？


}
