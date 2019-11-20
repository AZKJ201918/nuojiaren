package com.shopping.entity;

import lombok.Data;

import java.util.Date;

@Data
public class RetailWater {

    private Integer id;

    private String uuid;

    private String orderid;

    private Date createtime;

    private String content;
}
