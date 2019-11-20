package com.shopping.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CommercialEntity {

    private Integer id;

    private Integer cid;

    private Double subtract;

    private Integer fulld;

    private Double fullDiscount;

    private Integer postage;

    private Integer fulls;

    private Double fullSubtract;

    private Double discount;

    private String aid;

    private Double price;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GTM+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GTM+8")
    private Date endTime;
}
