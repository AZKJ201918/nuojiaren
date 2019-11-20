package com.shopping.dao;

import com.shopping.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShopCarMapper {
    @Select("select name,repertory from commodity where id=#{id}")
    CommodityEntity selectCommodity(String id);
    @Select("select id,name,subname,price,url from commodity where id in (${ids})")
    List<CommodityEntity> selectShopCar(@Param("ids") String ids);
    @Update("update orders set status=2,paytime=#{format} where orderid=#{orderId}")
    Integer updateOrderStatus(@Param("orderId") String orderId,@Param("format") String format);
    @Select("select repertory from commodity where id=#{id} for update")
    Integer selectRepertory(String id);
    @Select("select id from commodity")
    List<Integer> selectAllCommodityId();
    @Update("update commodity set repertory=#{repertory} where id=#{id}")
    Integer updateRepertory(@Param("repertory") Integer repertory,@Param("id") Integer id);
    @Select("select id,closetime from orders where status=1")
    List<OrderEntity> selectCloseTimeAndId();
    //把订单设置为过期
    @Update("update orders set status=0 where id=#{id}")
    Integer updateOrders(Integer id);
    @Select("select cid,num from ordercommodity where orderid=#{orderId}")
    List<OrderCommodityEntity> selectOrderCommodity(String orderId);
    @Select("select uid,price from orders where orderid=#{orderId}")
    OrderEntity selectPrice(String orderId);
    @Update("update wxuser set integral=integral+#{price} where uuid=#{uid}")
    int updateScore(OrderEntity order);
    @Select("select finalprice from orders where orderid=#{orderId}")
    Double selectFinalPrice(String orderId);
    @Select("select openid from wxuser where uuid=#{uuid}")
    String selectOpenid(String uuid);
    @Insert("insert into ordercommodity (orderid,cid,num,aid) values (#{orderId},#{cid},#{num},#{aid})")
    int insertOrderCommodity(OrderCommodityEntity orderCommodity);
    @Select("select cid from ordercommodity where orderid=#{orderId}")
    List<Integer> selectCid(String orderId);
    @Select("select aid from ordercommodity where orderid=#{orderId} and cid=#{id}")
    String selectAid(@Param("orderId") String orderId,@Param("id") Integer id);
    @Select("select aid from commercial where cid=#{id}")
    String selectCAid(Integer id);
    @Select("select id,repertory from commodity")
    List<CommodityEntity> selectIdAndRepertory();
    @Select("select volumn from volumn where cid=#{id}")
    Integer selectVolumn(Integer id);
    @Update("update commodity set repertory=repertory-#{volumn} where id=#{id}")
    int updateReper(@Param("volumn") Integer volumn,@Param("id") Integer id);//库存减去流水
    @Select("select cid,num from ordercommodity where orderid=#{orderId}")
    List<OrderCommodityEntity> selectCidAndNum(String orderId);
    @Select("select id from volumn where cid=#{cid}")
    Integer selectVolumnId(Integer cid);
    @Update("update volumn set volumn=volumn+#{num},totalvolumn=totalvolumn+#{num} where cid=#{cid}")//根据非主键cid修改流水
    int updateVolumn(OrderCommodityEntity orderCommodity);
    @Insert("insert into volumn (volumn,cid,totalvolumn) values (#{num},#{cid},#{num})")
    int insertVolumn(OrderCommodityEntity orderCommodity);
    @Select("select uid,cid from orders where orderid=#{orderId}")
    OrderEntity selectUUIDByOrderId(String orderId);
    @Select("select superiorid from wxuser where uuid=#{uuid}")
    String selectSuperByUUID(String uuid);
    @Select("select retail from commodity where id=#{id}")
    Integer selectRetail(Integer id);
    @Select("select parent,grand,parenttype,grandtype,outtime from retail where cid=#{id}")
    RetailEntity selectRetailInfo(Integer id);
    @Select("select wholeparent,wholegrand,parenttype,grandtype from wholeretail where cid=#{id}")
    WholeRetailEntity selectWholeRetail(Integer id);
    @Update("update wxuser set retailmoney=retailmoney+#{totalParentMoney} where uuid=#{superiorid}")
    int updateRetailMoney(@Param("totalParentMoney") Double totalParentMoney,@Param("superiorid") String superiorid);
    @Select("select nickname from wxuser where uuid=#{uid}")
    String selectWxname(String uid);
    @Insert("insert into retailwater (uuid,orderid,createtime,content) values (#{uuid},#{orderid},#{createtime},#{content})")
    int insertRetailWater(RetailWater retailWater);
    @Update("update wxuser set issign=0")
    void updateIsSign();
    @Update("update volumn set volumn=0")
    int changeVolumn();
    @Select("select isintegral,beretail from commodity where id=#{cid}")
    CommodityEntity selectIsintegralAndBeretail(Integer cid);
    @Select("select uid from orders where orderid=#{orderId}")
    String selectUid(String orderId);
    @Update("update wxuser set isretail=1 where uuid=#{uid}")
    int updateBeRetail(String uid);
}
