package com.shopping.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.util.StringUtils;
import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.BannerEntity;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.OptionEntity;
import com.shopping.entity.WxUser;
import com.shopping.service.HomePageService;
import com.shopping.util.JsonUtils;
import com.shopping.util.UrlUtils;
import com.shopping.util.getOpenIdutil;
import com.sun.jndi.toolkit.url.UrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.noggit.CharUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.shopping.util.EncodeUtil.getUserInfo;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "首页模块")
public class HomePageController {
    @Autowired
    private HomePageService homePageService;
    @Autowired
    private RedisTemplate redisTemplate;
    @ApiOperation(value = "轮播图",notes = "轮播图",httpMethod = "GET")
    @ApiImplicitParam
    @GetMapping("/banner")
    public ApiResult<List<BannerEntity>> banner(){
        ApiResult<List<BannerEntity>> result = new ApiResult<>();
        try {
            List<BannerEntity> homeBannerList= homePageService.findAllBanner();
            result.setData(homeBannerList);
            result.setMessage("轮播图搜索成功");
        } catch (SuperMarketException e) {
            e.printStackTrace();
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("服务器异常");
        }
        return result;
    }
    @ApiOperation(value = "商品展示",notes = "商品展示",httpMethod = "GET")
    @ApiImplicitParam
    @GetMapping("/commodity")
    public ApiResult<List<CommodityEntity>> commodity(Integer page){
        ApiResult<List<CommodityEntity>> result=new ApiResult();
        try {
            List<CommodityEntity> commodityList=homePageService.findAllCommodity(page);
            result.setData(commodityList);
            result.setMessage("商品搜索成功");
        } catch (SuperMarketException e) {
            e.printStackTrace();
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("后台服务器异常");
        }
        return result;
    }
    @ApiOperation(value = "首页按钮",notes = "首页按钮",httpMethod = "GET")
    @ApiImplicitParam
    @GetMapping("/button")
    public ApiResult <List<OptionEntity>> button(){
        ApiResult<List<OptionEntity>> result = new ApiResult<>();
        try {
            List<OptionEntity> optionList=homePageService.findAllOption();
            result.setData(optionList);
            result.setMessage("首页按钮搜索成功");
        } catch (SuperMarketException e) {
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("后台服务器异常");
        }
        return result;
    }
    @ApiOperation(value = "登录")
    @ApiImplicitParam
    @PostMapping("/login")
    public ApiResult<String> loginByWeixin(String code,String uuid,String encryptedData,String iv) {
        ApiResult<String> result = new ApiResult<>();
        //String code = reqMap.get("code");MapUtils.getString(configProperties, "url");//请求的地址MapUtils.getString(configProperties, "appId");//开发者对应的AppID MapUtils.getString(configProperties, "appSecret");//开发者对应的AppSecret MapUtils.getString(configProperties, "grant_type");
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        String appId ="wxa49b35cbb6b4708e" ;
        String appSecret ="06b176d0d2cd0a791ef5a4b5aaaf121f";
        String grant_type ="authorization_code";
        Map<String, Object> map = new HashMap<>();
        map.put("appid",appId);
        map.put("secret",appSecret);
        map.put("js_code",code);
        map.put("grant_type",grant_type);
        //调用微信接口获取openId用户唯一标识
        String wxReturnValue = UrlUtils.sendPost(url, map);
        Map<String,Object> tempMap = null;
        try {
            tempMap = JsonUtils.convertJson2Object(wxReturnValue, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(tempMap.containsKey("errcode")){
            String errcode = tempMap.get("errcode").toString();
            log.info("微信返回的错误码",errcode);
        }else if(tempMap.containsKey("session_key")){
            log.info("调用微信成功");
            //开始处理userInfo
            String openid = tempMap.get("openid").toString();
            System.out.println(openid);
            /*WxUser wxuser = new WxUser();
            String session_key = tempMap.get("session_key").toString();
            Map<String,String> userMap = getUserInfo(encryptedData, session_key, iv);
            String nickName = (String) userMap.get("nickName");
            Set<Map.Entry<String, Object>> entries = userMap.entrySet();
            for (Map.Entry<String, Object> set:entries){
                System.out.println(set.getKey()+"=="+set.getValue());
            }
            System.out.println(nickName);*/
            //wxuser.setWopenId(openid);
            //先查询openId存在不存在，存在不入库，不存在就入库
            String userUUID = homePageService.findUserByOpenid(openid);
            String session_key = "";
            session_key = tempMap.get("session_key").toString();
            Map<String,String> userMap = getUserInfo(encryptedData, session_key, iv);
            String nickName = userMap.get("nickName");
            String headimgurl = userMap.get("avatarUrl");
            ValueOperations ops = redisTemplate.opsForValue();
            if(userUUID != null){//用户信息已经初始化
                log.info("openId已经存在,不需要插入");
                WxUser user1 = new WxUser();
                if (uuid!=null) {//有分享人
                    String mySuperiorid = homePageService.findSuperioridByOpenid(openid);
                    if (mySuperiorid == null) {//没有父id
                        if (!userUUID.equals(uuid)) {
                            user1.setUuid(userUUID);
                            user1.setHeadimgurl(headimgurl);
                            user1.setNickname(nickName);
                            user1.setSuperiorid(uuid);
                            homePageService.modifyWeixinUser(user1);
                        }
                        result.setData(userUUID);
                    } else {//有父id
                        user1=new WxUser();
                        user1.setUuid(userUUID);
                        user1.setHeadimgurl(headimgurl);
                        user1.setNickname(nickName);
                        homePageService.modifyWeixinUser1(user1);
                        result.setData(userUUID);
                    }
                }else {//没有分享人
                    user1=new WxUser();
                    user1.setUuid(userUUID);
                    user1.setHeadimgurl(headimgurl);
                    user1.setNickname(nickName);
                    homePageService.modifyWeixinUser1(user1);
                    result.setData(userUUID);
                }
                ops.set(userUUID,userUUID,15L, TimeUnit.MINUTES);
            }else{//用户没有初始化
                String wxUserId = UUID.randomUUID().toString().replaceAll("-", "");//用户id
                log.info("openId不存在,插入数据库");
                //对encryptedData用户数据加解密
                //String encryptedData = reqMap.get("encryptedData");
               //String iv = reqMap.get("iv");
                WxUser user = new WxUser();

                //String headimgurl = userMap.get("headimgurl");
                //String gender = String.valueOf(userMap.get("gender"));
                //String gender = String.valueOf(userMap.get("gender"));
                //String province = userMap.get("province");
                //String city = userMap.get("city");
                //String country = userMap.get("country");
                //创建对象,将数据插入数据库中
                //Map<String, Object> dataMap = new HashMap<>();
                if (uuid!=null){//有分享人
                    String mySuperiorid=homePageService.findSuperioridByOpenid(openid);
                    if (mySuperiorid==null){//没有父id,首次进入不可能自己是自己的分享人
                         user.setUuid(wxUserId);
                         user.setHeadimgurl(headimgurl);
                         user.setNickname(nickName);
                         user.setOpenid(openid);
                         user.setSuperiorid(uuid);
                         result.setData(wxUserId);
                         homePageService.addWeixinUser(user);
                    }else {//有父id
                        user=new WxUser();
                        user.setUuid(wxUserId);
                        user.setHeadimgurl(headimgurl);
                        user.setNickname(nickName);
                        user.setOpenid(openid);
                        result.setData(wxUserId);
                        homePageService.addWeixinUser1(user);
                    }

                }else {//没有分享人
                    user=new WxUser();
                    user.setUuid(wxUserId);
                    user.setHeadimgurl(headimgurl);
                    user.setNickname(nickName);
                    user.setOpenid(openid);
                    result.setData(wxUserId);
                    homePageService.addWeixinUser1(user);
                }
                //Integer count1 =  wxUserMapper.insertWxUser(newUser);
                ops.set(wxUserId,wxUserId,15L,TimeUnit.MINUTES);
            }
        }
                return result;
        }

}
