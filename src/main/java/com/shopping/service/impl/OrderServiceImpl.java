package com.shopping.service.impl;

import com.shopping.dao.HomePageMapper;
import com.shopping.dao.IntegralCommodityMapper;
import com.shopping.dao.OrderMapper;
import com.shopping.dao.PriceMapper;
import com.shopping.entity.*;
import com.shopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private HomePageMapper homePageMapper;
    @Autowired
    private IntegralCommodityMapper integralCommodityMapper;



    @Override
    public Integer addOrder(OrderEntity order) {
        return orderMapper.insertOrder(order);
    }

    @Override
    public List<OrderEntity> findAllOrder(String uuid,Integer page,String status) {
        Integer start=(page-1)*10;
        List<OrderEntity> orderList = orderMapper.selectAllOrder(uuid, start, status);
        for (OrderEntity order:orderList){
            List<CommodityEntity> commodityList=orderMapper.selectCommodity(order.getCid());
            order.setCommodityList(commodityList);
        }
        return orderList;
    }

    @Override
    public OrderEntity findOneOrder(String id) {
        OrderEntity order=orderMapper.selectOneOrder(id);
        AddressEntity address=orderMapper.selectAddressById(String.valueOf(order.getAddressid()));
        order.setAddress(address);
        List<CommodityEntity> commodity = orderMapper.selectCommodity(order.getCid());
        List<IntegralCommodity> integralCommodityList=orderMapper.selectCidAndIntegral();
        for (CommodityEntity commodit:commodity){
            String orderId = order.getOrderid();
            //String cid = order.getCid();
          // List<Integer>ids=orderMapper.selectCid(orderId);
            Integer cid = commodit.getId();
            System.out.println(cid);
            System.out.println(orderId);
            CommercialEntity commercial = homePageMapper.selectActiveByCid(cid);
            Integer num=orderMapper.selectNum(cid,orderId);
            commodit.setCommercial(commercial);
            commodit.setNum(num);
            for (IntegralCommodity integralCommodity:integralCommodityList){
                Integer cid1 = integralCommodity.getCid();
                Integer integral = integralCommodity.getIntegral();
                if (cid==cid1){
                    commodit.setIntegral(integral);
                }
            }
        }
        order.setCommodityList(commodity);
        return order;
    }

    @Override
    public Integer removeOrder(String id) {
        return orderMapper.deleteOrder(id);
    }

    @Override
    public Double findPrice(String id) {
        return orderMapper.selectPrice(id);
    }

    @Override
    public int modifyOrder(String id) {
        return orderMapper.updateOrder(id);
    }

    @Override
    public Integer findIdOrder(String orderid) {
        return orderMapper.selectIdOrder(orderid);
    }

    @Override
    public String selectOrderIdById(String id) {
        return orderMapper.selectOrderIdById1(id);
    }

    @Override
    public List<OrderCommodityEntity> findCidAndNum(String orderId) {
        return orderMapper.selectCidAndNum(orderId);
    }

    @Override
    public Integer findStatus(Integer id) {
        return orderMapper.selectStatus(id);
    }

    @Override
    public void updateStatus(Integer id) {
        orderMapper.updateStatus(id);
    }

    @Override
    public void removeOrderCommodity(String orderId) {
        orderMapper.deleteOrderCommodity(orderId);
    }

    @Override
    public void modifyCommodityStatus(String id) {
        orderMapper.updateCommodityStatus(id);
    }

    @Override
    public boolean findOrderExsits(String id, String orderId, String uuid) {
        int count = orderMapper.selectOrderExsits(id, orderId, uuid);
        if (count>=1){
            return true;
        }
        return false;
    }

    @Override
    public AddressEntity findAddressIdExsits(String uuid, Integer addressid) {
        return orderMapper.selectAddressIdExists(uuid,addressid);
    }


}
