package com.shopping.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OrderCommodityEntity {

    private Integer id;

    private String orderId;

    private Integer cid;

    private Integer num;

    private String aid;

    private Integer oid;

    private Date createtime;//商品流水的创建时间
}
