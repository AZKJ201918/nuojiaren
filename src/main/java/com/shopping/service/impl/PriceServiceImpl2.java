package com.shopping.service.impl;

import com.shopping.service.PriceService;
import org.springframework.stereotype.Service;

@Service("post")
public class PriceServiceImpl2 implements PriceService {
    @Override
    public double countPrice(String id, Integer num,double totalPrice) {
        return 0;
    }
}
