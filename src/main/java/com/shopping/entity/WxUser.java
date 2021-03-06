package com.shopping.entity;

import lombok.Data;

import java.util.Date;

@Data
public class WxUser {

    private String uuid;

    private String openid;

    private String nickname;

    private String headimgurl;

    private String phone;

    private String superiorid;

    private Double integral;

    private Double retailMoney;

    private Double money;

    private Date createtime;

}
