package com.shopping.dao;

import com.shopping.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("insert into orders (uid,orderid,price,finalprice,addressid,createtime,closetime,cid,status) values (#{uid},#{orderid},#{price},#{finalprice},#{addressid},#{createtime},#{closetime},#{cid},#{status})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    Integer insertOrder(OrderEntity order) ;
    List<OrderEntity> selectAllOrder(@Param("uuid") String uuid,@Param("start") Integer start,@Param("status") String status);
    @Select("select id,name,url,price from commodity where id in (${cid})")
    List<CommodityEntity> selectCommodity(@Param("cid") String cid);
    @Select("select orderid,finalprice,addressid,cid,status from orders where id=#{id}")
    OrderEntity selectOneOrder(String id);
    @Select("select name,phone,province,city,area,detail from address where id=#{id}")
    AddressEntity selectAddressById(String id);
    @Delete("delete from orders where id=#{id}")
    Integer deleteOrder(String id);
    @Select("select price from commodity where id=#{id}")
    Double selectPrice(String id);
    @Update("update orders set status=0 where id=#{id}")
    int updateOrder(String id);
    @Select("select num from ordercommodity where orderid=#{orderid} and cid=#{cid}")
    Integer selectNum(@Param("cid") Integer cid,@Param("orderid") String orderid);
    @Select("select id from orders where orderid=#{orderid}")
    Integer selectIdOrder(String orderid);
    @Select("select cid from ordercommodity where orderid=#{orderId}")
    List<Integer> selectCid(String orderId);
    @Select("select orderid,uid from orders where id=#{id}")
    OrderEntity selectOrderIdById(String id);
    @Select("select orderid from orders where id=#{id}")
    String selectOrderIdById1(String id);
    @Select("select cid,num from ordercommodity where orderid=#{orderId}")
    List<OrderCommodityEntity> selectCidAndNum(String orderId);
    @Select("select status from orders where id=#{id}")
    Integer selectStatus(Integer id);
    @Update("update orders set status =5 where id=#{id}")
    int updateStatus(Integer id);
    @Select("select integral,cid from integralcommodity")
    List<IntegralCommodity> selectIntegralCommodity();
    @Update("update wxuser set integral+=#{integral} where uuid=#{uuid}")
    int updateIntegral(@Param("uuid") String uuid,@Param("integral") Integer integral);
    @Select("select integral,cid from integralcommodity")
    List<IntegralCommodity> selectCidAndIntegral();
    @Delete("delete from ordercommodity where orderid =#{orderId}")
    int deleteOrderCommodity(String orderId);
    @Update("update commodity set status=0 where id=#{id}")
    int updateCommodityStatus(String id);
    @Select("select count(*) from orders where id=#{id} and orderid=#{orderId} and uid=#{uuid}")
    int selectOrderExsits(@Param("id") String id, @Param("orderId") String orderId, @Param("uuid") String uuid);
    @Select("select province,city from address where uid=#{uuid} and id=#{addressid}")
    AddressEntity selectAddressIdExists(@Param("uuid") String uuid,@Param("addressid") Integer addressid);
    @Select("select status from orders where orderid=#{orderId} and uid=#{uuid}")
    OrderEntity selectOrder(@Param("orderId") String orderId,@Param("uuid") String uuid);
    @Update("update orders set status=4,recievetime=#{date} where orderid=#{orderId}")
    int recieveOrders(@Param("orderId") String orderId, @Param("date") Date date);
}
