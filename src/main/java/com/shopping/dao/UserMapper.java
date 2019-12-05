package com.shopping.dao;

import com.shopping.entity.AddressEntity;
import com.shopping.entity.Cash;
import com.shopping.entity.WxUser;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
@Mapper
public interface UserMapper {
    @Select("select openid from wxuser where uuid=#{uid}")
    WxUser findByPrimaryKey(Integer uid);
    @Select("select id from address where status=0 and uid=#{uid}")
    String selectMrAddress(String uid);
    @Insert("insert into address (name,phone,province,city,area,detail,uid,status) values (#{name},#{phone},#{province},#{city},#{area},#{detail},#{uid},#{status})")
    int insertAddress(AddressEntity address);
    @Select("select id,name,phone,province,city,area,detail,status from address where uid=#{uid}")
    List<AddressEntity> selectAllAddress(String uid);

    int updateAddress(AddressEntity address);
    @Delete("delete from address where id=#{id}")
    int deleteAddress(String id);
    @Update("update address set status=0 where id=#{id}")
    int updateAddressStatus(String id);
    @Update("update address set status=1 where status=0 and uid=#{uid}")
    int updateMrAddress(String uid);
    @Select("select money,retailmoney from wxuser where uuid=#{uuid}")
    WxUser selectRetailMoneyByUUID(String uuid);
    @Update("update wxuser set money=money+#{money} where uuid=#{uuid}")
    int updateMoneyByUUID(@Param("uuid") String uuid,@Param("money") Integer money);
    @Insert("insert into cash (cash,status,createtime,uuid) values (#{money},0,#{date},#{uuid})")
    int insertCash(@Param("money") Integer money, @Param("date") Date date,@Param("uuid") String uuid);
    @Select("select cash,status,createtime from cash where uuid=#{uuid} order by createtime DESC limit 5")
    List<Cash> selectCash(String uuid);
}
