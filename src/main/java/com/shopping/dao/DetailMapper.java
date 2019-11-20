package com.shopping.dao;

import com.shopping.entity.CommercialEntity;
import com.shopping.entity.CommodityEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DetailMapper {
    @Select("select name,url,price,sales,repertory,detailurl,specsurl,saleurl from commodity where status=1 and id=#{id}")
    List<CommodityEntity> selectDetailById(String id);
    @Select("select detailurl from detailbanner where cid=#{id} order by sort ASC")
    List<String> selectDetailBannerById(String id);
    @Select("select aid,startTime,endTime from commercial where cid=#{id}")
    CommercialEntity selectActiveById(String id);
    @Select("select type from activity where id in (${aid}) order by sort ASC")
    List<String> selectActiveSortByAid(@Param("aid") String aid);
    @Insert("insert into ordercommodity (orderid,cid,num) values (#{orderId},#{id},#{num})")
    int insertOrderCommodity(@Param("orderId") String orderId,@Param("id") String id,@Param("num") Integer num);
}
