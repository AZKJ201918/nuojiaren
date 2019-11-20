package com.shopping.service.impl;

import com.shopping.dao.PriceMapper;
import com.shopping.entity.CommercialEntity;
import com.shopping.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("subtract")
public class PriceServiceImpl1 implements PriceService {
    @Autowired
    private PriceMapper priceMapper;
    //单个商品的计算价格方法
    @Override
    public double countPrice(String id, Integer num,double totalPrice) {
        CommercialEntity commercial=priceMapper.selectPrice(id);
        Double subtract=commercial.getSubtract();
        Double zj=0.0;
        Double countPrice=0.0;
        if (totalPrice==0){
            zj=commercial.getPrice()*num;
        }else {
            zj=totalPrice;
        }
        countPrice=zj-commercial.getSubtract()*num;
        //totalPrice+=countPrice;
        return countPrice;
    }
    //多个商品的价格
}
