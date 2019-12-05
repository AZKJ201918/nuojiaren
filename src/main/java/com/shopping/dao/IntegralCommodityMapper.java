package com.shopping.dao;

import com.shopping.entity.CommodityEntity;
import com.shopping.entity.IntegralCommodity;
import com.shopping.entity.OrderCommodityEntity;
import com.shopping.entity.OrderEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IntegralCommodityMapper {
    @Select("select id,name,url,price,sales,repertory,subname from commodity where status=1 and isintegral=1")
    List<CommodityEntity> selectIntegralCommodity();
    @Select("select name,url,price,sales,repertory,subname,detailurl,specsurl,saleurl,postage from commodity where status=1 and id=#{id}")
    CommodityEntity selectIntegralCommodityDetail(Integer id);
    @Select("select integral,num from integralcommodity where cid=#{id}")
    IntegralCommodity selectNeedIntegral(Integer id);
    @Select("select integral from wxuser where uuid=#{uuid}")
    Integer selectUserIntegral(String uuid);
    @Select("select count(*) from orders where uid=#{uuid} and cid=#{id}")
    Integer selectChangeNum(@Param("id") Integer id,@Param("uuid") String uuid);
    @Select("select postage from commodity where id=#{id}")
    Double selectPrice(Integer id);
    @Insert("insert into orders (uid,orderid,price,finalprice,addressid,cid,status,createtime,closetime) values (#{uid},#{orderid},#{price},#{finalprice},#{addressid},#{cid},#{status},#{createtime},#{closetime})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insertOrder(OrderEntity order);
    @Insert("insert into ordercommodity (orderid,cid,num) values (#{orderId},#{cid},#{num})")
    int insertOrderCommodity(OrderCommodityEntity orderCommodity);
    @Insert("insert into orders (uid,orderid,price,finalprice,addressid,cid,status,createtime,closetime,paytime) values (#{uid},#{orderid},#{price},#{finalprice},#{addressid},#{cid},#{status},#{createtime},#{closetime},#{paytime})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insertOrderWithPaytime(OrderEntity order);
    @Update("update wxuser set integral=integral-#{integral} where uuid=#{uuid}")
    int updateUserIntegral(@Param("integral") Integer integral,@Param("uuid") String uuid);
    @Select("select integral from integralcommodity where cid=#{id}")
    Integer selectCommodityIntegral(Integer id);
}
