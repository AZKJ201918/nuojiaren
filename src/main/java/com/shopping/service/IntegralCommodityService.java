package com.shopping.service;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.IntegralCommodity;
import com.shopping.entity.OrderCommodityEntity;
import com.shopping.entity.OrderEntity;

import java.util.List;

public interface IntegralCommodityService {
    List<CommodityEntity> findIntegralCommodity() throws SuperMarketException;

    CommodityEntity findIntegralCommodityDetail(Integer id);

    IntegralCommodity findNeedIntegral(Integer id) throws SuperMarketException;

    Integer findUserIntegral(String uuid) throws SuperMarketException;

    Integer findChangeNum(Integer id, String uuid);

    Double findPrice(Integer id);

    void addOrder(OrderEntity order);

    void addOrderCommodity(OrderCommodityEntity orderCommodity);

    void addOrderWithPaytime(OrderEntity order);

    void modifyUserIntegral(Integer integral, String uuid);
}
