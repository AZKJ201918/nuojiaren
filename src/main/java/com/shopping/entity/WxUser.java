package com.shopping.entity;

import lombok.Data;

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


}
