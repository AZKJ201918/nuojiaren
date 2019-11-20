package com.shopping.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RetailEntity {

    private Integer id;

    private Double parent;

    private Double grand;

    private Integer parenttype;

    private Integer grandtype;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date outtime;

    private Integer cid;//与商品关联的主键

}
