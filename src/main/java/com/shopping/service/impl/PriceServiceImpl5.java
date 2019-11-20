package com.shopping.service.impl;

import com.shopping.dao.PriceMapper;
import com.shopping.entity.CommercialEntity;
import com.shopping.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("fulldiscount")
public class PriceServiceImpl5 implements PriceService {

    @Autowired
    private PriceMapper priceMapper;
    @Override
    public double countPrice(String id, Integer num,double totalPrice) {
        CommercialEntity commercial=priceMapper.findPriceFullDiscount(id);
        Double zj=0.0;
        Double countPrice=0.0;
        Double fulld=zj*commercial.getFulld();
        if (totalPrice==0){
            zj=commercial.getPrice()*num;
        }else {
            zj=totalPrice;
        }
        if (zj>fulld){
            countPrice=zj*commercial.getFullDiscount();
        }else {
            countPrice=zj;
        }
        //totalPrice+=countPrice;
        return countPrice;
    }
}
