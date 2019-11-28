package com.shopping.service;


import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.OrderCommodityEntity;
import com.shopping.entity.OrderEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ShopCarService {
    CommodityEntity findCommodity(String id);

    List<CommodityEntity> findShopCar(Set<String> set, Map<String,Integer> carMap) throws SuperMarketException;

    Integer modifyOrderStatus(String orderId);

    Integer findRepertory(String id);

    void modifyRepertory();

    void modifyOutDateOrders();

    void modifyNum(String orderId);

    OrderEntity findPrice(String orderId);

    Double findFinalPrice(String orderId);

    String findOpenid(String uuid);

    int addOrderCommodity(OrderCommodityEntity orderCommodity);

    List<Integer> findCid(String orderId);

    String findAid(String orderId, Integer id);

    String findCAid(Integer id);

    void modifyWater();

    List<OrderCommodityEntity> findCidAndNum(String orderId);

    Integer findVolumnId(Integer cid);

    void modifyVolumn(OrderCommodityEntity orderCommodity);

    void addVolumn(OrderCommodityEntity orderCommodity);

    void retailMoney(String orderId);

    CommodityEntity findIsintegralAndBeretail(Integer cid);

    String findUid(String orderId);

    void modifyBeRetail(String uid);

    Integer findVolumn(Integer id);

    void addVolumnWater(OrderCommodityEntity orderCommodity);

    void addRecord(Map<String, Integer> map);
}
