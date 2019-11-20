package com.shopping.entity;

import lombok.Data;

@Data
public class BannerEntity {
    //轮播图主键
   private Integer id;
   //轮播图链接
   private String viewurl;
   //跳转类型
   private Integer linktype;
   //跳转地址
   private String linkurl;
   //跳转商品号
   private String linkid;
    //排序
   private Integer sort;
}
