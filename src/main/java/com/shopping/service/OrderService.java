package com.shopping.service;

import com.shopping.entity.AddressEntity;
import com.shopping.entity.CommercialEntity;
import com.shopping.entity.OrderCommodityEntity;
import com.shopping.entity.OrderEntity;

import java.util.List;

public interface OrderService {

    Integer addOrder(OrderEntity order);


    List<OrderEntity> findAllOrder(String uuid,Integer page,String status);

    OrderEntity findOneOrder(String id);

    Integer removeOrder(String id);

    Double findPrice(String id);

    int modifyOrder(String id);

    Integer findIdOrder(String orderid);

    String selectOrderIdById(String id);

    List<OrderCommodityEntity>  findCidAndNum(String orderId);

    Integer findStatus(Integer id);

    void updateStatus(Integer id);

    void removeOrderCommodity(String orderId);

    void modifyCommodityStatus(String id);

    boolean findOrderExsits(String orderId, String uuid);

    AddressEntity findAddressIdExsits(String uuid, Integer addressid);

    OrderEntity findOrder(String orderId, String uuid);

    void signOrders(String orderId);
}
