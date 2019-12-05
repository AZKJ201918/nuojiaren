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
    @Select("select name,url,price,sales,repertory,detailurl,specsurl,saleurl,subname from commodity where status=1 and id=#{id}")
    List<CommodityEntity> selectDetailById(String id);
    @Select("select detailurl from detailbanner where cid=#{id} order by sort ASC")
    List<String> selectDetailBannerById(String id);
    @Select("select aid,startTime,endTime from commercial where cid=#{id}")
    CommercialEntity selectActiveById(String id);
    @Select("select type from activity where id in (${aid}) order by sort ASC")
    List<String> selectActiveSortByAid(@Param("aid") String aid);
    @Insert("insert into ordercommodity (orderid,cid,num) values (#{orderId},#{id},#{num})")
    int insertOrderCommodity(@Param("orderId") String orderId,@Param("id") String id,@Param("num") Integer num);
    @Select("select buy from commodity where id=#{id1}")
    Integer selectCommodityNum(String id1);
    @Select("select orderid from orders where uid=#{uuid} and status>0")
    List<String> selectOrderIdByUUID(String uuid);
    @Select("select ifnull(sum(num),0) from wxuserxg where cid=#{id1} and uuid=#{uuid}")
    Integer selectXgNum(@Param("id1") String id1,@Param("uuid") String uuid);
    @Select("select price from commodity where id=#{id}")
    Integer selectPrice(Integer id);
}
