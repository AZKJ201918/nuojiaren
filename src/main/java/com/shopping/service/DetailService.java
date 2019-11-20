package com.shopping.service;

import com.shopping.entity.CommercialEntity;
import com.shopping.entity.CommodityEntity;

import java.util.List;

public interface DetailService {
    List<CommodityEntity> findDetailById(String id);

    List<String> findDetailBannerById(String id);

    CommercialEntity findActiveById(String id);

    List<String> findActiveSortByAid(String aid);

    int addOrderCommodity(String orderId, String id, Integer num);
}
