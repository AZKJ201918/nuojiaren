package com.shopping.service.impl;

import com.shopping.dao.DetailMapper;
import com.shopping.dao.HomePageMapper;
import com.shopping.dao.PriceMapper;
import com.shopping.entity.CommercialEntity;
import com.shopping.entity.CommodityEntity;
import com.shopping.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailServiceImpl implements DetailService{
    @Autowired
    private HomePageMapper homePageMapper;
    @Autowired
    private DetailMapper detailMapper;
    @Autowired
    private PriceMapper priceMapper;
    @Override
    public List<CommodityEntity> findDetailById(String id) {
        List<CommodityEntity> commodityList = detailMapper.selectDetailById(id);
        for (CommodityEntity commodity:commodityList){
            CommercialEntity commercial= homePageMapper.selectActiveByCid(Integer.parseInt(id));
           /* String[] split = commercial.getAid().split(",");
            for (String aid:split){
                if (aid.equals("1")){
                    CommercialEntity commercialEntity = priceMapper.selectPrice(aid);
                }
            }*/
           commodity.setCommercial(commercial);
        }
        return commodityList;
    }

    @Override
    public List<String> findDetailBannerById(String id) {
        return detailMapper.selectDetailBannerById(id);
    }

    @Override
    public CommercialEntity findActiveById(String id) {
        return detailMapper.selectActiveById(id);
    }

    @Override
    public List<String> findActiveSortByAid(String aid) {
        return detailMapper.selectActiveSortByAid(aid);
    }

    @Override
    public int addOrderCommodity(String orderId, String id, Integer num) {
        return detailMapper.insertOrderCommodity(orderId,id,num);
    }
}
