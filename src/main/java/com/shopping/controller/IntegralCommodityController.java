package com.shopping.controller;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.*;
import com.shopping.service.DetailService;
import com.shopping.service.IntegralCommodityService;
import com.shopping.service.OrderService;
import com.shopping.service.ShopCarService;
import com.shopping.util.DateUtil;
import com.shopping.util.PosPrepay;
import com.shopping.util.PrePayDemo;
import com.shopping.util.ZkClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@Slf4j
@EnableTransactionManagement
@Api(value = "积分商城模块")
public class IntegralCommodityController {

    @Autowired
    private IntegralCommodityService integralCommodityService;
    @Autowired
    private DetailService detailService;
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ShopCarService shopCarService;
    @Autowired
    private OrderService orderService;


    @ApiOperation(value = "积分商品展示",notes = "积分商品展示",httpMethod = "GET")
    @ApiImplicitParam
    @GetMapping("/integralCommodity")
    public ApiResult integralCommodity(){
        ApiResult<Object> result = new ApiResult<>();
        List<CommodityEntity> commodityList= null;
        try {
            commodityList = integralCommodityService.findIntegralCommodity();
            result.setData(commodityList);
            result.setMessage("积分商品查看成功");
        } catch (SuperMarketException e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
        } catch (Exception e){
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看积分商品详情",notes = "查看积分商品详情",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/integralCommodityDetail")
    public ApiResult integralCommodityDetail(Integer id){
        ApiResult<Object> result = new ApiResult<>();
        HashMap<String, Object> map = new HashMap<>();
        try {
            CommodityEntity commodity=integralCommodityService.findIntegralCommodityDetail(id);
            List<String> detailBanner = detailService.findDetailBannerById(String.valueOf(id));
            map.put("commodity",commodity);
            map.put("detailBanner",detailBanner);
            result.setMessage("积分商品详情查看成功");
            result.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "积分兑换商品",notes = "积分兑换商品",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/changeCommodity")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult changeCommodity(Integer id,String uuid,Integer addressid){
        ApiResult<Object> result = new ApiResult<>();
        InterProcessMutex lock=null;
        HashOperations hos = redisTemplate.opsForHash();
        ValueOperations ops = redisTemplate.opsForValue();
        try {
            lock = new InterProcessMutex(zkClient.getZkClient(), Constants.USER_REGISTER_DISTRIBUTE_LOCK_PATH2);
            boolean retry = true;
            do{
            if (lock.acquire(3000, TimeUnit.MILLISECONDS)) {
                String ruuid = (String) ops.get("uuid:"+uuid);
                if (uuid == null || !uuid.equals(ruuid)) {
                    result.setCode(Constants.RESP_STATUS_BADREQUEST);
                    result.setMessage("用户未登录");
                    if (lock!=null){
                        lock.release();
                    }
                    return result;
                }
                AddressEntity address = orderService.findAddressIdExsits(uuid, addressid);
                if (address == null) {
                    if (lock!=null){
                        lock.release();
                    }
                    throw new SuperMarketException("地址与用户信息不对应");
                }
                IntegralCommodity integralCommodity = integralCommodityService.findNeedIntegral(id);//商品需要的积分
                Integer userIntegral = integralCommodityService.findUserIntegral(uuid);//用户的积分
                Integer integral = integralCommodity.getIntegral();
                if (userIntegral < integral) {
                    if (lock!=null){
                        lock.release();
                    }
                    throw new SuperMarketException("你的积分不够");
                }
                Integer num = integralCommodity.getNum();//限购的次数
                Integer count = integralCommodityService.findChangeNum(id, uuid);//找已经换购的次数
                if (count >= num) {
                    if (lock!=null){
                        lock.release();
                    }
                    throw new SuperMarketException("你达到限购的次数，无法兑换");
                }
                Double price = integralCommodityService.findPrice(id);//积分商品需要的价格
                //生成订单，并且扣除积分调用支付接口
                integralCommodityService.modifyUserIntegral(integral, uuid);
                String orderId = DateUtil.getOrderIdByTime();
                OrderEntity order = new OrderEntity();
                order.setUid(uuid);
                order.setPrice(price);
                order.setFinalprice(price);
                order.setAddressid(addressid);
                order.setCid(String.valueOf(id));
                order.setCreatetime(new Date());
                order.setClosetime(DateUtil.plusDay2(1));
                order.setOrderid(orderId);
                if (price > 0) {
                    order.setStatus(1);
                    integralCommodityService.addOrder(order);
                    OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                    orderCommodity.setCid(id);
                    orderCommodity.setNum(1);
                    orderCommodity.setOrderId(orderId);
                    integralCommodityService.addOrderCommodity(orderCommodity);//生成订单副单
                    Integer reper = (Integer) hos.get("repertory:"+id + "", "repertory");
                    if (reper == null) {
                        reper = shopCarService.findRepertory(id + "");
                        Integer volumn=shopCarService.findVolumn(id);
                        reper-=volumn;
                    }
                    if (reper < 1) {
                        orderService.modifyCommodityStatus(String.valueOf(id));
                    }
                    //最终支付
                    PosPrepay posPrepay = new PosPrepay();
                    String openid = shopCarService.findOpenid(uuid);
                    int v = (int) (price * 100);
                    posPrepay.setTotal_fee(v);
                    posPrepay.setTerminal_trace(order.getOrderid());
                    posPrepay.setOperator_id(openid);
                    posPrepay.setTerminal_time(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                    reper -= 1;
                    hos.put("repertory:"+id + "", "repertory", reper);//扣除库存
                    Map map = (Map) PrePayDemo.posPrePayRe(posPrepay);
                    map.put("id", order.getId());
                    ops.set("orderId:"+orderId, map.get("out_trade_no"), 3L, TimeUnit.MINUTES);
                    result.setData(map);
                    result.setMessage("支付成功");
                } else {
                    order.setStatus(2);
                    order.setPaytime(new Date());
                    integralCommodityService.addOrderWithPaytime(order);
                    OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                    orderCommodity.setCid(id);
                    orderCommodity.setNum(1);
                    orderCommodity.setOrderId(orderId);
                    integralCommodityService.addOrderCommodity(orderCommodity);//生成订单副单
                    Integer reper = (Integer) hos.get("repertory:"+id + "", "repertory");
                    if (reper == null) {
                        reper = shopCarService.findRepertory(id + "");
                        Integer volumn = shopCarService.findVolumn(id);
                        reper-=volumn;
                    }
                    if (reper < 1) {
                        orderService.modifyCommodityStatus(String.valueOf(id));
                    }
                    reper -= 1;
                    hos.put("repertory:"+id + "", "repertory", reper);//扣除库存
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", order.getId());
                    result.setData(map);
                    result.setMessage("支付成功");
                }
                retry = false;
            }
        }while (retry);
        if (null != lock) {
            lock.release();
        }
    } catch (SuperMarketException e) {
        log.error("用户注册异常",e);
        result.setMessage(e.getMessage());
        result.setData(Constants.RESP_STATUS_BADREQUEST);
        if (null != lock) {
            try {
                lock.release();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }catch (Exception e) {
        log.error("用户注册异常",e);
        result.setMessage("后台服务器异常");
        result.setData(Constants.RESP_STATUS_INTERNAL_ERROR);
        if (null != lock) {
            try {
                lock.release();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
        return result;
    }
}
