package com.shopping.entity;

import lombok.Data;

@Data
public class IntegralCommodity {

    private Integer id;

    private Integer integral;

    private Integer cid;

    private Integer num;//商品限购的次数
}
