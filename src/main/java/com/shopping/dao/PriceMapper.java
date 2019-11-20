package com.shopping.dao;

import com.shopping.entity.CommercialEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//根据商品的id查看价格和各项优惠
@Mapper
public interface PriceMapper {
    @Select("select cd.price,ca.subtract from commodity cd inner join commercial ca on cd.id=ca.cid where cd.id=#{id}")
    CommercialEntity selectPrice(String id);
    @Select("select cd.price,ca.fulls,ca.fullsubtract from commodity cd inner join commercial ca on  cd.id=ca.cid where cd.id=#{id}")
    CommercialEntity selectPriceFullSubtract(String id);
    @Select("select cd.price,ca.discount from commodity cd inner join commercial ca on cd.id=ca.cid where cd.id=#{id}")
    CommercialEntity selectPriceDiscount(String id);
    @Select("select cd.price,ca.fulld,ca.fulldiscount from commodity cd inner join commercial ca on cd.id=ca.cid where cd.id=#{id}")
    CommercialEntity findPriceFullDiscount(String id);
}
