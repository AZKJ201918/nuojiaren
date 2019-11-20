package com.shopping.service.impl;

import com.shopping.dao.PriceMapper;
import com.shopping.entity.CommercialEntity;
import com.shopping.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("fullsubtract")
public class PriceServiceImpl3 implements PriceService {
    @Autowired
    private PriceMapper priceMapper;
    @Override
    public double countPrice(String id, Integer num,double totalPrice) {
         Double countPrice=0.0;
         Double zj=0.0;
        CommercialEntity commercial = priceMapper.selectPriceFullSubtract(id);
         if (totalPrice!=0){
             zj=totalPrice;
         }else {
             zj=commercial.getPrice()*num;
         }
         if (zj>=commercial.getFulls()){
             countPrice=zj-commercial.getFullSubtract();
         }else {
             countPrice=zj;
         }
          //totalPrice+=countPrice;
         return countPrice;
    }
}
