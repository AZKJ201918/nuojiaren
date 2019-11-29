package com.shopping.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderEntity {

    private Integer id;

    private String uid;

    private String orderid;

    private Double price;

    private Double finalprice;

    private Integer addressid;

    private String cid;

    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date closetime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date paytime;

    private String courier;

    private String company;

    private AddressEntity address;

    private List<CommodityEntity> commodityList;
}
