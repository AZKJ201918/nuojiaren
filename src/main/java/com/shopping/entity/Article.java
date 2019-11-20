package com.shopping.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Article {

    private Integer id;

    private String title;

    private String pictrue;

    private String articleurl;

    private String author;

    private Date createtime;
}
