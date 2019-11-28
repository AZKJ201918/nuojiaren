package com.shopping.service.impl;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.IntegralCommodityMapper;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.IntegralCommodity;
import com.shopping.entity.OrderCommodityEntity;
import com.shopping.entity.OrderEntity;
import com.shopping.service.IntegralCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntegralCommodityServiceImpl implements IntegralCommodityService{

    @Autowired
    private IntegralCommodityMapper integralCommodityMapper;
    @Override
    public List<CommodityEntity> findIntegralCommodity() throws SuperMarketException {
        List<CommodityEntity> commodityList = integralCommodityMapper.selectIntegralCommodity();
        if (commodityList==null){
            throw new SuperMarketException("没有积分兑换商品");
        }
        return commodityList;
    }

    @Override
    public CommodityEntity findIntegralCommodityDetail(Integer id) throws SuperMarketException {
        CommodityEntity commodity = integralCommodityMapper.selectIntegralCommodityDetail(id);
        if (commodity==null){
            throw new SuperMarketException("没有积分商品");
        }
        Integer integral=integralCommodityMapper.selectCommodityIntegral(id);
        commodity.setIntegral(integral);
        return commodity;
    }

    @Override
    public IntegralCommodity findNeedIntegral(Integer id) throws SuperMarketException {
        IntegralCommodity integral = integralCommodityMapper.selectNeedIntegral(id);
        if (integral==null){
            throw new SuperMarketException("该商品不是积分商品");
        }
        return integral;
    }

    @Override
    public Integer findUserIntegral(String uuid) throws SuperMarketException {
        Integer userIntegral = integralCommodityMapper.selectUserIntegral(uuid);
        if (userIntegral==null){
            throw new SuperMarketException("该用户不存在");
        }
        return userIntegral;
    }

    @Override
    public Integer findChangeNum(Integer id, String uuid) {
        return integralCommodityMapper.selectChangeNum(id,uuid);
    }

    @Override
    public Double findPrice(Integer id) {
        return integralCommodityMapper.selectPrice(id);
    }

    @Override
    public void addOrder(OrderEntity order) {
        integralCommodityMapper.insertOrder(order);
    }

    @Override
    public void addOrderCommodity(OrderCommodityEntity orderCommodity) {
        integralCommodityMapper.insertOrderCommodity(orderCommodity);
    }

    @Override
    public void addOrderWithPaytime(OrderEntity order) {
        integralCommodityMapper.insertOrderWithPaytime(order);
    }

    @Override
    public void modifyUserIntegral(Integer integral, String uuid) {
        integralCommodityMapper.updateUserIntegral(integral,uuid);
    }
}
