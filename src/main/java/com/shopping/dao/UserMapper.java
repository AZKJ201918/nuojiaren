package com.shopping.dao;

import com.shopping.entity.AddressEntity;
import com.shopping.entity.WxUser;
import org.apache.ibatis.annotations.*;

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
}
