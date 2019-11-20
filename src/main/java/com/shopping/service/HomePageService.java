package com.shopping.service;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.BannerEntity;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.OptionEntity;
import com.shopping.entity.WxUser;

import java.util.List;

public interface HomePageService {
    List<BannerEntity> findAllBanner() throws SuperMarketException;

    List<CommodityEntity> findAllCommodity(Integer page) throws SuperMarketException;

    List<OptionEntity> findAllOption() throws SuperMarketException;

    String findUserByOpenid(String openid);

    String findSuperioridByOpenid(String openid);

    String findShareIdidByUUID(String uuid);

    int addWeixinUser(WxUser user);

    int addWeixinUser1(WxUser user);

    int modifyWeixinUser(WxUser user1);

    int modifyWeixinUser1(WxUser user1);

    WxUser findwxnameAndHeadimgurl(String uid);
}
