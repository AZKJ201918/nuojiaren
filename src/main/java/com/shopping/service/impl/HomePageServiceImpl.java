package com.shopping.service.impl;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.HomePageMapper;
import com.shopping.entity.*;
import com.shopping.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HomePageServiceImpl implements HomePageService{
    @Autowired
    private HomePageMapper homePageMapper;

    @Override
    public List<BannerEntity> findAllBanner() throws SuperMarketException {
        List<BannerEntity> homeBannerList = homePageMapper.selectAllBanner();
        if (homeBannerList!=null){
            return homeBannerList;
        }else {
            throw new SuperMarketException("没有轮播图");
        }
    }

    @Override
    public List<CommodityEntity> findAllCommodity(Integer page) throws SuperMarketException {
        Integer start=(page-1)*8;
        List<CommodityEntity>commodityList= homePageMapper.selectAllCommodity(start);
        if (commodityList!=null){
            for (CommodityEntity commodity:commodityList){
                CommercialEntity commercial= homePageMapper.selectSubtractByCid(commodity.getId());
                if (commercial!=null){
                      long startTime = commercial.getStartTime().getTime();
                      long endTime = commercial.getEndTime().getTime();
                      long now = new Date().getTime();
                       System.out.println("subtract"+commercial.getSubtract());
                       // Double endPrice=commodity.getPrice()-subtract;
                       // commodity.setEndPrice(endPrice);
                       if (now>=startTime&&now<=endTime){
                           commodity.setSubtract(commercial.getSubtract());
                           commodity.setAid(commercial.getAid());
                           commodity.setDiscount(commercial.getDiscount());
                       }
                }
            }
            return commodityList;
        }else {
            throw new SuperMarketException("没有找到商品");
        }
    }

    @Override
    public List<OptionEntity> findAllOption() throws SuperMarketException {
        List<OptionEntity> optionList=homePageMapper.selectAllOption();
        if (optionList!=null) {
            return optionList;
        }else {
            throw new SuperMarketException("没有找到按钮信息");
        }
    }

    @Override
    public WxUser findUserByOpenid(String openid) {
        return homePageMapper.selectUserByOpenid(openid);
    }

    @Override
    public String findSuperioridByOpenid(String openid) {
        return homePageMapper.selectSuperioridByOpenid(openid);
    }

    @Override
    public String findShareIdidByUUID(String uuid) {
        return homePageMapper.selectShareIdByUUID(uuid);
    }

    @Override
    public int addWeixinUser(WxUser user) {
        return homePageMapper.insertWeixinUser(user);
    }

    @Override
    public int addWeixinUser1(WxUser user) {
        return homePageMapper.insertWeixinUser1(user);
    }

    @Override
    public int modifyWeixinUser(WxUser user1) {
        return homePageMapper.updateWeixinUser(user1);
    }

    @Override
    public int modifyWeixinUser1(WxUser user1) {
        return homePageMapper.updateWeixinUser1(user1);
    }

    @Override
    public WxUser findwxnameAndHeadimgurl(String uid) {
        return homePageMapper.selectwxnameAndHeadimgurl(uid);
    }
}
