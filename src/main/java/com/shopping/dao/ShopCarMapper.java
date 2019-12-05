package com.shopping.dao;

import com.shopping.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Select("select ifnull(sum(num),0) from volumnwater where cid=#{id} and date(createtime)=#{format}")
    Integer selectVolumn(@Param("id") Integer id,@Param("format")String format);
    @Update("update commodity set repertory=repertory-#{volumn} where id=#{id}")
    int updateReper(@Param("volumn") Integer volumn,@Param("id") Integer id);//库存减去流水
    @Select("select cid,num from ordercommodity where orderid=#{orderId}")
    List<OrderCommodityEntity> selectCidAndNum(String orderId);
    @Select("select id from volumn where cid=#{cid}")
    Integer selectVolumnId(Integer cid);
    @Update("update volumn set totalvolumn=totalvolumn+#{volumn} where cid=#{id}")//根据非主键cid修改流水
    int updateVolumn(@Param("volumn") Integer volumn,@Param("id") Integer id);
    @Insert("insert into volumn (cid,totalvolumn) values (#{id},#{volumn})")
    int insertVolumn(@Param("volumn") Integer volumn,@Param("id") Integer id);
    @Select("select uid,cid from orders where orderid=#{orderId}")
    OrderEntity selectUUIDByOrderId(String orderId);
    @Select("select superiorid from wxuser where uuid=#{uuid}")
    String selectSuperByUUID(String uuid);
    @Select("select retail from commodity where id=#{id}")
    Integer selectRetail(Integer id);
    @Select("select parent,grand,parenttype,grandtype,outtime from retail where cid=#{id}")
    RetailEntity selectRetailInfo(Integer id);
    @Select("select wholeparent,wholegrand,parenttype,grandtype from wholeretail")
    WholeRetailEntity selectWholeRetail();
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
    @Insert("insert into volumnwater (num,cid,createtime) values (#{num},#{cid},#{createtime})")
    int insertVolumnWater(OrderCommodityEntity orderCommodity);
    @Select("select num from volumnwater where createtime>#{date1}  and createtime<=#{date} and cid=#{id}")
    List<Integer> selectVolumnWater(@Param("id") Integer id,@Param("date1") Date date1,@Param("date") Date date);
    @Insert("insert into record (visit,register,createtime) values (#{visit},#{register},#{date})")
    int insertRecord(@Param("visit")Integer visit,@Param("register") Integer register,@Param("date") Date date);
    @Select("select orderid,sendouttime from orders")
    List<OrderEntity> selectOutRecieveOrders();
    @Update("update orders set status=4 where orderid=#{orderid}")
    int updateOrderRecieve(String orderid);
    @Insert("insert into wxuserxg (uuid,cid,num,orderid) values (#{uid},#{cid},#{num},#{orderId})")
    int insertXgNum(@Param("uid") String uid, @Param("cid") Integer cid, @Param("num") Integer num, @Param("orderId") String orderId);
    @Delete("delete from wxuserxg where uuid=#{uuid} and orderid=#{orderId} and cid=#{cid}")
    int deleteWxUserXg(@Param("uuid") String uuid,@Param("orderId") String orderId,@Param("cid") Integer cid);
}
