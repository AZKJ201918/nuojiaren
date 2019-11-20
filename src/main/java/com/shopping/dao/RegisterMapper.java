package com.shopping.dao;

import com.shopping.entity.CommodityEntity;
import com.shopping.entity.RegisterEntity;
import com.shopping.entity.RetailWater;
import com.shopping.entity.WxUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RegisterMapper {
    @Select("select name from register where uuid=#{uuid}")
    String selectRegisterByPhone(String uuid);
    @Insert("insert into register (uuid,name,phone,idcard) values (#{uuid},#{name},#{phone},#{idcard})")
    int insertRegister(RegisterEntity registerEntity);
    @Select("select bankid from bank where rid=#{uuid} and status=0")
    String selectMrBankId(String uuid);
    @Insert("insert into bank (bankid,rid,status) values (#{bankid},#{uuid},0)")
    int insertMrBankId(@Param("bankid") String bankid, @Param("uuid") String uuid);
    @Select("select rid from bank where bankid=#{bankid} and status=1")
    String selectBankId(String bankid);
    @Insert("insert into bank (bankid,rid,status) values (#{bankid},#{uuid},1)")
    int insertBankId(@Param("bankid") String bankid,@Param("uuid") String uuid);
    @Update("update wxuser set integral=integral+#{integral} ,issign=1 where uuid=#{uuid}")
    int updateIntegralAndIsSign(@Param("uuid") String uuid,@Param("integral") Integer integral);
    @Select("select issign from wxuser where uuid=#{uuid}")
    Integer selectIsSign(String uuid);
    @Select("select integral from intergral")
    Integer selectIntergral();
    @Select("select content from retailwater where uuid=#{uuid} order by createtime DESC limit #{start},10")
    List<RetailWater> selectRetailWater(@Param("uuid") String uuid, @Param("start") Integer start);
    @Select("select integral,retailmoney from wxuser where uuid=#{uuid}")
    WxUser selectIntegralAndRetailMoney(String uuid);
    @Select("select nickname,headimgurl from wxuser where superiorid =#{uuid} limit #{start},10")
    List<WxUser> selectNext(@Param("uuid") String uuid,@Param("start") Integer start);
    @Select("select isretail from wxuser where uuid=#{uuid}")
    Integer selectIsRetail(String uuid);
    @Select("select id from commodity where beretail=1")
    List<CommodityEntity> selectBeRetail();
}
