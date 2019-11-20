package com.shopping.dao;

import com.shopping.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@Mapper
public interface HomePageMapper {
    @Select("select viewurl,linktype,linkurl,linkid,sort from slideshow order by sort ASC")
    List<BannerEntity> selectAllBanner();
    //商品没有下架，并且不是积分商品
    @Select("select id,name,url,price,sales,repertory,subname from commodity where status=1 and isintegral=0 order by createTime DESC limit #{start},8")
    List<CommodityEntity> selectAllCommodity(Integer start);
    @Select("select subtract,aid from commercial where cid=#{id}")
    CommercialEntity selectSubtractByCid(Integer id);
    @Select("select name,imgurl,linkurl,linkid,linktype from options where status=1")
    List<OptionEntity> selectAllOption();
    @Select("select uuid from wxuser where openid=#{openid}")
    String selectUserByOpenid(String openid);
    @Select("select superiorid from wxuser where openid=#{openid}")
    String selectSuperioridByOpenid(String openid);
    @Select("select")
    String selectShareIdByUUID(String uuid);
    @Insert("insert into wxuser (uuid,openid,nickname,headimgurl,superiorid) values (#{uuid},#{openid},#{nickname},#{headimgurl},#{superiorid})")
    int insertWeixinUser(WxUser user);
    @Insert("insert into wxuser (uuid,openid,nickname,headimgurl) values (#{uuid},#{openid},#{nickname},#{headimgurl})")
    int insertWeixinUser1(WxUser user);
    @Update("update wxuser set nickname=#{nickname},headimgurl=#{headimgurl},superiorid=#{superiorid} where uuid=#{uuid}")
    int updateWeixinUser(WxUser user1);
    @Update("update wxuser set nickname=#{nickname},headimgurl=#{headimgurl} where uuid=#{uuid}")
    int updateWeixinUser1(WxUser user1);
    @Select("select nickname,headimgurl from wxuser where uuid=#{uid} ")
    WxUser selectwxnameAndHeadimgurl(String uid);
    @Select("select subtract,aid,fulld,fulldiscount,fulls,fullsubtract,discount from commercial where cid=#{id}")
    CommercialEntity selectActiveByCid(Integer id);
}
