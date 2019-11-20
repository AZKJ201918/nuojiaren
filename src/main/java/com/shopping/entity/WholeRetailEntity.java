package com.shopping.entity;

import lombok.Data;

@Data
public class WholeRetailEntity {

    private Integer id;

    private Double wholeparent;

    private Double wholegrand;

    private Integer parenttype;

    private Integer grandtype;

    private Integer cid;
}
