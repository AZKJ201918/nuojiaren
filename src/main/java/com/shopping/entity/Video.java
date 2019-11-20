package com.shopping.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Video {

    private Integer id;

    private String videourl;

    private Date createtime;
}
