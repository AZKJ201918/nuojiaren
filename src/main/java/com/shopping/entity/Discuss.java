package com.shopping.entity;

import lombok.Data;

@Data
public class Discuss {
    private String id;

    private Integer cid;

    private String details;

    private String uid;

    private String wxname;

    private String wximg;

    private String detailsImg;

    private Integer evaluate;

    private String plusDetails;//追加评论

    private String orderId;
}
