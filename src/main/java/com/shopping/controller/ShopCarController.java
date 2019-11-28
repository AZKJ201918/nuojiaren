package com.shopping.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.CommercialEntity;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.OrderCommodityEntity;
import com.shopping.entity.OrderEntity;
import com.shopping.service.DetailService;
import com.shopping.service.OrderService;
import com.shopping.service.ShopCarService;
import com.shopping.util.PosPrepay;
import com.shopping.util.PosPrepayRe;
import com.shopping.util.PrePayDemo;

import com.shopping.util.XmlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "购物车模块")
public class ShopCarController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ShopCarService shopCarService;
    @Autowired
    private DetailService detailService;
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "新增购物车", notes = "新增购物车", httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/insertShopCar")
    public ApiResult insertShopCar(String uuid, String id, Integer num) {
        ApiResult<Object> result = new ApiResult<>();
        HashOperations hos = redisTemplate.opsForHash();
        ValueOperations ops = redisTemplate.opsForValue();
       /* String userUid = (String) ops.get(uuid);
        if (userUid==null){
            result.setData(1);
            result.setMessage("用户未登录");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            return result;
        }*/
        //无需到数据库查询这些信息
        /*CommodityEntity commodity=shopCarService.findCommodity(id);
          if (commodity==null){
            result.setData(2);
            result.setMessage("商品不存在");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            return result;
        }*/
        /*if (num-commodity.getRepertory()<0){
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("商品库存量不足，最多可以加入购物车的数量是"+commodity.getRepertory());
            result.setData(3);
            return result;
        }*/
        //暂时隐藏起来
        String ruuid = (String) ops.get("uuid:"+uuid);
        if (uuid==null||ruuid==null||!uuid.equals(ruuid)){
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
            result.setMessage("用户未登录");
            return result;
        }
        Integer repertory= (Integer) hos.get("repertory:"+id,"repertory");
        System.out.println(id+"库存是"+repertory);
        if (repertory==null){
            Integer reper=shopCarService.findRepertory(id);
            Integer volumn = shopCarService.findVolumn(Integer.parseInt(id));
            repertory=reper-volumn;
            hos.put("repertory:"+id,"repertory",repertory);
        }
        if (repertory<=0){//没有库存，把商品下架
            orderService.modifyCommodityStatus(id);
        }
        if (repertory-num<0){
            result.setMessage("商品库存不足，您最多可以添加到购物车的数量是"+repertory);
            result.setData(3);
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            return result;
        }
        Boolean flag1 = hos.hasKey("shopCar:"+uuid,"shopCar:"+id);
        Integer carNum=null;
        if (flag1){
            carNum = (Integer) hos.get("shopCar:"+uuid,"shopCar:"+id);
        }
        if (carNum != null) {
            carNum += num;
            hos.put("shopCar:"+uuid,"shopCar:"+id, carNum);
        } else {
            hos.put("shopCar:"+uuid, "shopCar:"+id, num);
        }
        result.setMessage("购物车新增成功");
        result.setData(4);
        return result;
    }

    @ApiOperation(value = "查看购物车", notes = "查看购物车", httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadShopCar")
    public ApiResult loadShopCar(String id, String uuid) {
        ApiResult<Object> result = new ApiResult<>();
        HashMap<String, Object> map = new HashMap<>();
        try {
            HashOperations hos = redisTemplate.opsForHash();
            Map<String, Integer> carMap = hos.entries("shopCar:"+uuid);
            Set<String> set = carMap.keySet();
            List<CommodityEntity> commodity = shopCarService.findShopCar(set, carMap);
            map.put("commodity", commodity);
            result.setData(map);
            result.setMessage("购物车查看成功");
        } catch (SuperMarketException e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }

    @ApiOperation(value = "删除购物车", notes = "删除购物车", httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/deleteShopCar")
    public ApiResult deleteShopCar(String uuid, String id) {
        ApiResult<Object> result = new ApiResult<>();
        ValueOperations ops = redisTemplate.opsForValue();
        String ruuid = (String) ops.get("uuid:"+uuid);
        if (uuid==null||!uuid.equals(ruuid)){
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
            result.setMessage("用户未登录");
            return result;
        }
        try {
            HashOperations hos = redisTemplate.opsForHash();
            Boolean flag = hos.hasKey("shopCar:"+uuid,"shopCar:"+id);
            if (flag){
                hos.delete("shopCar:"+uuid,"shopCar:"+id);
                result.setMessage("删除购物车成功");
            }else {
                result.setMessage("删除购物车失败");
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }

    @ApiOperation(value = "修改购物车数量", notes = "修改购物车数量", httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/updateShopCar")
    public ApiResult updateShopCar(String uuid, String id, Integer num) {
        ApiResult<Object> result = new ApiResult<>();
        ValueOperations ops = redisTemplate.opsForValue();
        String ruuid = (String) ops.get("uuid:"+uuid);
        if (uuid==null||!uuid.equals(ruuid)){
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
            result.setMessage("用户未登录");
            return result;
        }
        try {
            HashOperations hos = redisTemplate.opsForHash();
            System.out.println(num.longValue());
            Integer repertory= (Integer) hos.get("repertory:"+id,"repertory");
            System.out.println(id+"库存是"+repertory);
            if (repertory==null){
                Integer reper=shopCarService.findRepertory(id);
                Integer volumn = shopCarService.findVolumn(Integer.parseInt(id));
                repertory=reper-volumn;
                hos.put("repertory:"+id,"repertory",repertory);
            }
            if (repertory<=0){//没有库存，把商品下架
                orderService.modifyCommodityStatus(id);
            }
            if (repertory-num<0){
                result.setMessage("商品库存不足，您最多可以添加到购物车的数量是"+repertory);
                result.setData(3);
                result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                return result;
            }
            hos.put("shopCar:"+uuid,"shopCar:"+id, num);
            result.setMessage("购物车数量修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }

    @ApiOperation(value = "支付", notes = "支付", httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/pay")
    public ApiResult pay(String uuid,String orderId) {
        ApiResult<Object> result = new ApiResult<>();
        ValueOperations ops = redisTemplate.opsForValue();
        try {
            PosPrepay posPrepay = new PosPrepay();
            List<Integer> ids=shopCarService.findCid(orderId);
            for (Integer id:ids){
                String aid=shopCarService.findAid(orderId,id);
                CommercialEntity commercial= detailService.findActiveById(id + "");
                String aid1=commercial.getAid();
                long startTime = commercial.getStartTime().getTime();
                long endTime = commercial.getEndTime().getTime();
                long now = new Date().getTime();
                if (aid!=null&&!aid.equals(aid1)){
                    result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                    result.setMessage("活动已过期");
                    return result;
                }
                if (now<startTime||now>endTime){
                    result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                    result.setMessage("活动已过期");
                    return result;
                }
            }
            Double finalPricer=shopCarService.findFinalPrice(orderId);
            String openid=shopCarService.findOpenid(uuid);
            //finalPricer=finalPricer*100;
            posPrepay.setTotal_fee((int)(finalPricer*100));
            System.out.println(finalPricer*100);
            posPrepay.setOperator_id(openid);
            posPrepay.setTerminal_trace(orderId);
            posPrepay.setTerminal_time(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            Map posPrepayRe1 = (Map) PrePayDemo.posPrePayRe(posPrepay);
            ops.set(orderId,posPrepayRe1.get("out_trade_no"),3L, TimeUnit.MINUTES);
            result.setMessage("支付成功");
            result.setData(posPrepayRe1);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("支付失败");
            result.setData(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }

    @ApiOperation(value = "异步回调", notes = "异步回调", httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/annocy")
    public Map callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ApiResult result = new ApiResult();
        System.out.println("进入异步回调");
        Map<Object, Object> map = new HashMap<>();
        String inputLine = "";
        String notityXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notityXml += inputLine;
            }
            request.getReader().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("异步回调XML信息：" + notityXml);
        if (!notityXml.isEmpty()) {
            //解析并读取统一下单中的参数信息
            Map<String, Object> prepayMap = XmlUtil.getPrepayMapInfo(notityXml);
            JSONObject jsonObject = JSON.parseObject(notityXml);
            System.out.println("jsonObject"+jsonObject);
            if (!jsonObject.isEmpty()) {
                String orderId = jsonObject.get("terminal_trace") + "";//订单号
                System.out.println("异步回调订单号"+orderId);
                String resCode = jsonObject.get("result_code") + "";
                String returnCode = jsonObject.get("return_code") + "";
                ValueOperations ops = redisTemplate.opsForValue();
                String  out_trade_no= (String) ops.get("orderId:"+orderId);
                if (out_trade_no==null){
                    map.put("return_msg","没有支付,无法回调");
                    map.put("return_code","01");
                    return map;
                }
                // System.out.println("解析并读取统一下单中的参数信息:" + orderId + "===" + resCode + "===" + returnCode);
                System.out.println("异步回调的订单号:"+orderId);
                shopCarService.modifyOrderStatus(orderId);//修改订单状态为代发货,已付款，并且记录订单成交时间
               // shopCarService.modifyNum(orderId);
                List<OrderCommodityEntity> orderCommodityList=shopCarService.findCidAndNum(orderId);
                for (OrderCommodityEntity orderCommodity:orderCommodityList){//把订单的商品的数量记录在流水里
                    /*Integer id=shopCarService.findVolumnId(orderCommodity.getCid());
                    if(id!=null){
                        shopCarService.modifyVolumn(orderCommodity);
                    }else {
                        shopCarService.addVolumn(orderCommodity);
                    }*/
                    shopCarService.addVolumnWater(orderCommodity);
                }
                CommodityEntity commodity=null;
                boolean flag=false;
                boolean flag1=false;
                for (OrderCommodityEntity orderCommodity:orderCommodityList){
                    commodity=shopCarService.findIsintegralAndBeretail(orderCommodity.getCid());
                    if (commodity.getIsintegral()==0){//非积分商品
                        flag=true;
                    }
                    if (commodity.getBeretail()==1){
                        flag1=true;
                    }
                }
                if (flag){//不是积分商品
                    OrderEntity order = shopCarService.findPrice(orderId);//把用户消费的积分加进去
                    //分销加钱
                    shopCarService.retailMoney(orderId);
                }
                if (flag1){//能够成为分销商
                     String uid=shopCarService.findUid(orderId);
                     shopCarService.modifyBeRetail(uid);
                }
                map.put("return_code","01");
                map.put("return_msg","回调成功");
            }
           /* System.out.println("进入到printWriter");
            response.reset();
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            String parse="<xml>\n" +
                    "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "<return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
            printWriter.write(parse);
            printWriter.flush();
            result.setMessage("回调成功");*/
            //回调中业务逻辑完毕
        } else {
            //result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            map.put("return_msg","回调失败");
        }
        return map;
    }
}

