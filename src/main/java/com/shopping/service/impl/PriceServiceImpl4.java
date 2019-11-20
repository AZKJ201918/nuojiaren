package com.shopping.service.impl;

import com.shopping.dao.PriceMapper;
import com.shopping.entity.CommercialEntity;
import com.shopping.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("discount")
public class PriceServiceImpl4 implements PriceService{
    @Autowired
    private PriceMapper priceMapper;
    @Override
    public double countPrice(String id, Integer num,double totalPrice) {
        Double zj=0.0;
        CommercialEntity commercial=priceMapper.selectPriceDiscount(id);
        if (totalPrice==0){
            zj=commercial.getPrice()*num;
        }else {
            zj=totalPrice;
        }
        Double countPrice=zj*commercial.getDiscount();
        //totalPrice+=countPrice;
        System.out.println("折扣后的价格"+totalPrice);
        return countPrice;
    }
}
