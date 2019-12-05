package com.shopping.controller;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.*;
import com.shopping.service.DetailService;
import com.shopping.service.OrderService;
import com.shopping.service.PriceService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "订单模块")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private DetailService detailService;
    @Autowired
    private Map<String,PriceService> priceServiceMap;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private ShopCarService shopCarService;


    /*@ApiOperation(value = "生成订单" ,notes = "根据微信小程序传值生成订单",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("imputedPrice")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult createOrder(String uuid,String id,Integer num,Integer addressid){
        ApiResult<Object> result = new ApiResult<>();
        InterProcessMutex lock=null;
        ValueOperations ops = redisTemplate.opsForValue();
        try {
            String ruuid = (String) ops.get(uuid);
            if (uuid==null||!uuid.equals(ruuid)){
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                result.setMessage("用户未登录");
                return result;
            }
            lock = new InterProcessMutex(zkClient.getZkClient(), Constants.USER_REGISTER_DISTRIBUTE_LOCK_PATH);
            boolean retry = true;
            HashOperations hos = redisTemplate.opsForHash();
            do{
                if (lock.acquire(3000, TimeUnit.MILLISECONDS)){
                    Integer repertory = (Integer) hos.get(id, "repertory");
                    if (repertory==0){//没有库存，把商品下架
                        orderService.modifyCommodityStatus(id);
                    }
                    if (repertory-num<0){
                        if (lock!=null){
                            lock.release();
                        }
                        result.setMessage("商品库存不够");
                        result.setData(id);
                        result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                        return result;
                    }
                    String aid=detailService.findActiveById(id);//商品的aid
                    System.out.println(aid);
                    List<String> actives=detailService.findActiveSortByAid(aid);
                    double totalPrice=0;
                    double price=0;
                    Double danjia = orderService.findPrice(id);
                    price=danjia*num;
                    if (actives.contains("postage")){
                        for (String active:actives){
                            if (priceServiceMap.get(active.trim())!=null) {
                                totalPrice = priceServiceMap.get(active.trim()).countPrice(id, num, totalPrice);
                            }
                        }
                    }else {
                        for (String active:actives){
                            System.out.println(active);
                            System.out.println(priceServiceMap.get(active));
                            if (priceServiceMap.get(active.trim())!=null) {
                                totalPrice = priceServiceMap.get(active.trim()).countPrice(id, num, totalPrice);
                                System.out.println(totalPrice);
                            }
                        }
                        //不包邮，把邮费查出来加在总价上
                    }
                    OrderEntity order = new OrderEntity();
                    order.setUid(uuid);
                    order.setAddressid(addressid);
                    order.setFinalprice(totalPrice);
                    order.setOrderid(DateUtil.getOrderIdByTime());//生成订单号
                    order.setStatus(1);
                    order.setCreatetime(new Date());
                    System.out.println(new Date());
                    order.setCid(id);
                    order.setPrice(price);
                    order.setClosetime(DateUtil.plusDay2(1));
                    orderService.addOrder(order);
                    //把商品订单的数量信息，直接入库
                    Integer oid=orderService.findIdOrder(order.getOrderid());
                    OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                    orderCommodity.setCid(Integer.parseInt(id));
                    orderCommodity.setNum(num);
                    orderCommodity.setAid(aid);
                    orderCommodity.setOrderId(order.getOrderid());
                    orderCommodity.setOid(oid);
                    shopCarService.addOrderCommodity(orderCommodity);
                    Integer reper= (Integer) hos.get(id,"repertory");
                    reper-=num;
                    hos.put(id,"repertory",reper);
                    //最终支付
                    PosPrepay posPrepay=new PosPrepay();
                    String openid = shopCarService.findOpenid(uuid);
                    int v = (int) (totalPrice * 100);
                    posPrepay.setTotal_fee(v);
                    posPrepay.setTerminal_trace(order.getOrderid());
                    posPrepay.setOperator_id(openid);
                    posPrepay.setTerminal_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    Object o = PrePayDemo.posPrePayRe(posPrepay);
                    result.setData(o);
                    result.setMessage("支付成功");
                }
                retry = false;
            }while (retry);
            if(null != lock){
                lock.release();
            }
        } catch (Exception e) {
            log.error("用户注册异常",e);
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            if(null != lock){
                try {
                    lock.release();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }*/
    @ApiOperation(value = "多个商品生成订单" ,notes = "生成订单",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/manyCreateOrder")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult manyCreateOrder(/*String uuid, @RequestParam("ids") List<String> ids, Integer addressid,String id1,Integer number*/@RequestBody Map<String,Object> dataMap){
        InterProcessMutex lock=null;
        ApiResult<Object> result = new ApiResult<>();
        try {
            lock = new InterProcessMutex(zkClient.getZkClient(), Constants.USER_REGISTER_DISTRIBUTE_LOCK_PATH1);
            boolean retry = true;
            do{
                boolean flag=lock.acquire(3000, TimeUnit.MILLISECONDS);
                if (flag){
                    HashOperations hos = redisTemplate.opsForHash();
                    ValueOperations ops = redisTemplate.opsForValue();
                    String uuid= (String) dataMap.get("uuid");
                    String id1= (String) dataMap.get("id1");
                    List<String> ids= (List<String>) dataMap.get("ids");
                    Integer addressid= (Integer) dataMap.get("addressid");
                    Integer number= (Integer) dataMap.get("number");
                    String ruuid = (String) ops.get("uuid:"+uuid);
                    if (uuid==null||!uuid.equals(ruuid)){
                        if (lock!=null){
                            lock.release();
                        }
                        result.setCode(Constants.RESP_STATUS_BADREQUEST);
                        result.setMessage("用户未登录");
                        return result;
                    }
                    AddressEntity address=orderService.findAddressIdExsits(uuid,addressid);
                    if (address==null){
                        if (lock!=null){
                            lock.release();
                        }
                        result.setMessage("地址与用户信息不对应");
                        result.setCode(Constants.RESP_STATUS_BADREQUEST);
                        return result;
                    }
                    if (id1!=null&&number!=null){
                        Integer repertory = (Integer) hos.get("repertory:"+id1, "repertory");
                        if (repertory == null) {
                            repertory = shopCarService.findRepertory(id1 + "");
                            Integer volumn=shopCarService.findVolumn(Integer.parseInt(id1));
                            repertory-=volumn;
                        }
                        if (repertory<=0){//没有库存，把商品下架
                            orderService.modifyCommodityStatus(id1);
                        }
                        if (repertory-number<0){
                            if (lock!=null){
                                lock.release();
                            }
                            result.setMessage("商品库存不够");
                            result.setData(id1);
                            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                            return result;
                        }
                        Integer num=detailService.findCommodityNum(id1);//商品的限购次数
                        if (num!=null){
                            Integer onum=detailService.findXgNum(id1,uuid);//用户的购买次数
                            if (onum+number>=num){
                                if (lock!=null){
                                    lock.release();
                                }
                                result.setMessage("达到商品限购次数,暂不能购买");
                                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                                return result;
                            }
                        }
                        CommercialEntity commercial=detailService.findActiveById(id1);//商品的aid
                        String aid="";
                        double totalPrice = 0;
                        double price = 0;
                        Double danjia = orderService.findPrice(id1);
                        if (commercial!=null) {
                            aid = commercial.getAid();
                            long startTime = commercial.getStartTime().getTime();
                            long endTime = commercial.getEndTime().getTime();
                            long now = new Date().getTime();
                            System.out.println(aid);
                            if (aid == null || aid.equals("") || now < startTime || now > endTime) {
                                totalPrice = danjia * number;
                                price = danjia * number;
                            }
                            if (aid != null && !aid.equals("") && now >= startTime && now <= endTime) {
                                List<String> actives = detailService.findActiveSortByAid(aid);
                                price = danjia * number;
                                if (actives.contains("postage")) {
                                    for (String active : actives) {
                                        if (priceServiceMap.get(active.trim()) != null) {
                                            totalPrice = priceServiceMap.get(active.trim()).countPrice(id1, number, totalPrice);
                                        }
                                    }
                                } else {
                                    for (String active : actives) {
                                        System.out.println(active);
                                        System.out.println(priceServiceMap.get(active));
                                        if (priceServiceMap.get(active.trim()) != null) {
                                            totalPrice = priceServiceMap.get(active.trim()).countPrice(id1, number, totalPrice);
                                            System.out.println(totalPrice);
                                        }
                                    }
                                    //不包邮，把邮费查出来加在总价上
                                }
                            }
                            OrderEntity order = new OrderEntity();
                            order.setUid(uuid);
                            order.setAddressid(addressid);
                            order.setFinalprice(totalPrice);
                            order.setOrderid(DateUtil.getOrderIdByTime());//生成订单号
                            order.setStatus(1);
                            order.setCreatetime(new Date());
                            System.out.println(new Date());
                            order.setCid(id1);
                            order.setPrice(price);
                            order.setClosetime(DateUtil.plusDay2(1));
                            orderService.addOrder(order);
                            //把商品订单的数量信息，直接入库
                            Integer oid = orderService.findIdOrder(order.getOrderid());
                            OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                            orderCommodity.setCid(Integer.parseInt(id1));
                            orderCommodity.setNum(number);
                            if (aid != null && !aid.equals("") && now >= startTime && now <= endTime) {
                                orderCommodity.setAid(aid);
                            }
                            orderCommodity.setOrderId(order.getOrderid());
                            orderCommodity.setOid(oid);
                            shopCarService.addOrderCommodity(orderCommodity);
                            Integer reper = (Integer) hos.get("repertory:" + id1, "repertory");
                            if (reper == null) {
                                reper = shopCarService.findRepertory(id1 + "");
                                //Integer volumn=shopCarService.findVolumn(Integer.parseInt(id));
                                //reper-=volumn;
                                if (reper == null) {
                                    reper = 0;
                                }
                                Integer volumn = shopCarService.findVolumn(Integer.parseInt(id1));
                                reper -= volumn;
                            }
                            shopCarService.addXgNum(uuid, Integer.parseInt(id1), number, order.getOrderid());
                            reper -= number;
                            hos.put("repertory:" + id1, "repertory", reper);
                            //最终支付
                            PosPrepay posPrepay = new PosPrepay();
                            String openid = shopCarService.findOpenid(uuid);
                            int v = (int) (totalPrice * 100);
                            posPrepay.setTotal_fee(v);
                            posPrepay.setTerminal_trace(order.getOrderid());
                            posPrepay.setOperator_id(openid);
                            posPrepay.setTerminal_time(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                            Map map = (Map) PrePayDemo.posPrePayRe(posPrepay);
                            map.put("id", order.getId());
                            ops.set("orderId:" + order.getOrderid(), map.get("out_trade_no"), 3L, TimeUnit.MINUTES);
                            result.setData(map);
                            result.setMessage("支付成功");
                        }else {
                            totalPrice = danjia * number;
                            price = danjia * number;
                            OrderEntity order = new OrderEntity();
                            order.setUid(uuid);
                            order.setAddressid(addressid);
                            order.setFinalprice(totalPrice);
                            order.setOrderid(DateUtil.getOrderIdByTime());//生成订单号
                            order.setStatus(1);
                            order.setCreatetime(new Date());
                            System.out.println(new Date());
                            order.setCid(id1);
                            order.setPrice(price);
                            order.setClosetime(DateUtil.plusDay2(1));
                            orderService.addOrder(order);
                            //把商品订单的数量信息，直接入库
                            Integer oid = orderService.findIdOrder(order.getOrderid());
                            OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                            orderCommodity.setCid(Integer.parseInt(id1));
                            orderCommodity.setNum(number);
                            orderCommodity.setOrderId(order.getOrderid());
                            orderCommodity.setOid(oid);
                            shopCarService.addOrderCommodity(orderCommodity);
                            Integer reper = (Integer) hos.get("repertory:" + id1, "repertory");
                            if (reper == null) {
                                reper = shopCarService.findRepertory(id1 + "");
                                //Integer volumn=shopCarService.findVolumn(Integer.parseInt(id));
                                //reper-=volumn;
                                if (reper == null) {
                                    reper = 0;
                                }
                                Integer volumn = shopCarService.findVolumn(Integer.parseInt(id1));
                                reper -= volumn;
                            }
                            shopCarService.addXgNum(uuid, Integer.parseInt(id1), number, order.getOrderid());
                            reper -= number;
                            hos.put("repertory:" + id1, "repertory", reper);
                            //最终支付
                            PosPrepay posPrepay = new PosPrepay();
                            String openid = shopCarService.findOpenid(uuid);
                            int v = (int) (totalPrice * 100);
                            posPrepay.setTotal_fee(v);
                            posPrepay.setTerminal_trace(order.getOrderid());
                            posPrepay.setOperator_id(openid);
                            posPrepay.setTerminal_time(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                            Map map = (Map) PrePayDemo.posPrePayRe(posPrepay);
                            map.put("id", order.getId());
                            ops.set("orderId:" + order.getOrderid(), map.get("out_trade_no"), 3L, TimeUnit.MINUTES);
                            result.setData(map);
                            result.setMessage("支付成功");
                        }
                    }
                    if (ids!=null){
                        double finalPrice=0;
                        String idString="";
                        double price=0;//记录商品的总价
                        String orderId = DateUtil.getOrderIdByTime();
                        for (String id:ids){
                            double totalPrice=0;
                            idString+=id+",";
                            Integer num = (Integer) hos.get("shopCar:"+uuid,id);
                            Integer repertory = (Integer) hos.get("repertory:"+id, "repertory");
                            if (repertory == null) {
                                repertory = shopCarService.findRepertory(id + "");
                                //Integer volumn=shopCarService.findVolumn(Integer.parseInt(id));
                                //repertory-=volumn;
                                Integer volumn=shopCarService.findVolumn(Integer.parseInt(id1));
                                repertory-=volumn;
                            }
                            if (repertory<=0){//没有库存，把商品下架
                                orderService.modifyCommodityStatus(id);
                            }
                            if (repertory-num<0){
                                result.setMessage("库存不足");
                                result.setData(id);
                                result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                                if (lock!=null){
                                    lock.release();
                                }
                                return result;
                            }
                            Integer num1=detailService.findCommodityNum(id);//商品的限购次数
                            if (num1!=null){
                                Integer onum=detailService.findXgNum(id,uuid);//用户的购买次数
                                if (onum+num>=num1){//本次的购买次数大于限购次数
                                    throw new SuperMarketException(id);
                                }
                            }
                            CommercialEntity commercial= detailService.findActiveById(id);
                            //detailService.addOrderCommodity(orderId,id,num);
                            Double danjia=orderService.findPrice(id);
                            if (commercial!=null){
                                String aid=commercial.getAid();
                                long startTime = commercial.getStartTime().getTime();
                                long endTime = commercial.getEndTime().getTime();
                                long now = new Date().getTime();
                                System.out.println(aid);
                                if (aid==null||aid.equals("")||now<startTime||now>endTime){
                                    totalPrice=danjia*num;
                                    price+=danjia*num;
                                }
                                if (aid!=null&&!aid.equals("")&&now>=startTime&&now<=endTime){//有活动并且没有过期
                                    List<String> actives=detailService.findActiveSortByAid(aid);
                                    if (actives.contains("postage")){
                                        for (String active:actives){
                                            if (priceServiceMap.get(active.trim())!=null) {
                                                totalPrice = priceServiceMap.get(active.trim()).countPrice(id, num, totalPrice);
                                            }
                                        }
                                    }else {
                                        for (String active:actives){
                                            System.out.println(active);
                                            System.out.println(priceServiceMap.get(active));
                                            if (priceServiceMap.get(active.trim())!=null) {
                                                totalPrice = priceServiceMap.get(active.trim()).countPrice(id, num, totalPrice);
                                                System.out.println(totalPrice);
                                            }
                                        }
                                        //不包邮，把邮费查出来加在总价上
                                    }
                                    price+=danjia*num;
                                }
                                finalPrice+=totalPrice;
                                OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                                orderCommodity.setCid(Integer.parseInt(id));
                                orderCommodity.setNum(num);
                                if (aid!=null&&!aid.equals("")&&now>=startTime&&now<=endTime){
                                    orderCommodity.setAid(aid);
                                }
                                orderCommodity.setOrderId(orderId);
                                shopCarService.addOrderCommodity(orderCommodity);
                                shopCarService.addXgNum(uuid,Integer.parseInt(id),num,orderId);
                            }else {
                                totalPrice=danjia*num;
                                price+=danjia*num;
                                finalPrice+=totalPrice;
                                OrderCommodityEntity orderCommodity = new OrderCommodityEntity();
                                orderCommodity.setCid(Integer.parseInt(id));
                                orderCommodity.setNum(num);
                                orderCommodity.setOrderId(orderId);
                                shopCarService.addOrderCommodity(orderCommodity);
                                shopCarService.addXgNum(uuid,Integer.parseInt(id),num, orderId);
                            }
                        }
                        int i = idString.lastIndexOf(",");
                        String idsub = idString.substring(0, i);
                        OrderEntity order = new OrderEntity();
                        order.setCid(idsub);
                        order.setUid(uuid);
                        order.setAddressid(addressid);
                        order.setFinalprice(finalPrice);
                        order.setOrderid(orderId);//生成订单号
                        order.setStatus(1);
                        order.setCreatetime(new Date());
                        order.setPrice(price);
                        order.setClosetime(DateUtil.plusDay2(1));
                        orderService.addOrder(order);
                        result.setMessage("订单生成成功");
                        //Integer oid=orderService.findIdOrder(orderId);
                        //shopCarService.modifyOid(oid,orderId);无需加入oid，已经有唯一标识orderId
                        //订单完成立即扣除库存
                        for (String id:ids){
                            Integer reper= (Integer) hos.get("repertory:"+id,"repertory");//库存
                            Integer num = (Integer) hos.get("shopCar:"+uuid,id);//购物车数量
                            if (reper == null) {
                                reper = shopCarService.findRepertory(id + "");
                                //Integer volumn=shopCarService.findVolumn(Integer.parseInt(id));
                                //reper-=volumn;
                                Integer volumn=shopCarService.findVolumn(Integer.parseInt(id1));
                                reper-=volumn;
                            }
                            reper-=num;
                            hos.put("repertory:"+id,"repertory",reper);
                            hos.delete("shopCar:"+uuid,id);//清空购物车信息
                        }
                        //最终支付
                        PosPrepay posPrepay=new PosPrepay();
                        String openid = shopCarService.findOpenid(uuid);
                        int v = (int) (finalPrice * 100);
                        posPrepay.setTotal_fee(v);
                        posPrepay.setTerminal_trace(orderId);
                        posPrepay.setOperator_id(openid);
                        posPrepay.setTerminal_time(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                        Map map = (Map) PrePayDemo.posPrePayRe(posPrepay);
                        map.put("id",order.getId());
                        ops.set("orderId:"+orderId,map.get("out_trade_no"),3L,TimeUnit.MINUTES);
                        result.setData(map);
                        result.setMessage("支付成功");
                        System.out.println("走到支付完成");
                    }
                    System.out.println("支付到了这里");
                    retry = false;
                }

            }while (retry);
            if (null != lock) {
                lock.release();
            }
        } catch (SuperMarketException e) {
            log.error("用户注册异常",e);
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_BADREQUEST2);
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
    @ApiOperation(value = "查看所有订单" ,notes = "查看所有订单",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadAllOrder")
    public ApiResult loadAllOrder(String uuid,Integer page,String status){
        ApiResult result = new ApiResult();
        try {
            List<OrderEntity> orderList=orderService.findAllOrder(uuid,page,status);
            result.setData(orderList);
            result.setMessage("查看订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看某个订单详情" ,notes = "查看某个订单详情",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadOrderDetail")
    public ApiResult loadOrderDetail(String id){
        ApiResult<Object> result = new ApiResult<>();
        try {
            OrderEntity order=orderService.findOneOrder(id);
            result.setData(order);
            result.setMessage("订单详情查看成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "删除" ,notes = "删除订单",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/deleteOrder")
    public ApiResult deleteOrder(String id,String orderId,String uuid){
        ApiResult<Object> result = new ApiResult<>();
        ValueOperations ops = redisTemplate.opsForValue();
        try {
            String ruuid = (String) ops.get("uuid:"+uuid);
            if (uuid==null||!uuid.equals(ruuid)){
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                result.setMessage("用户未登录");
                return result;
            }
            boolean flag=orderService.findOrderExsits(orderId,uuid);
            if (flag==true){
                orderService.removeOrder(id);
                orderService.removeOrderCommodity(orderId);
                result.setMessage("订单删除成功");
            }else {
                result.setMessage("订单不是本人的，不可以删除");
                result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "取消订单" ,notes = "取消订单",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/modifyOrder")
    public ApiResult modifyOrder(String id,String orderId,String uuid){
        InterProcessMutex lock=null;
        ApiResult result=new ApiResult();
        try {
            lock = new InterProcessMutex(zkClient.getZkClient(), Constants.USER_REGISTER_DISTRIBUTE_LOCK_PATH3);
            boolean retry = true;
            do{
                if (lock.acquire(3000, TimeUnit.MILLISECONDS)) {
                    HashOperations hos = redisTemplate.opsForHash();
                    ValueOperations ops = redisTemplate.opsForValue();
                    String ruuid = (String) ops.get("uuid:" + uuid);
                    if (uuid == null || !uuid.equals(ruuid)) {
                        result.setCode(Constants.RESP_STATUS_BADREQUEST);
                        result.setMessage("用户未登录");
                        if (lock!=null){
                            lock.release();
                        }
                        return result;
                    }
                    boolean flag = orderService.findOrderExsits(orderId, uuid);
                    if (flag == true) {
                        orderService.modifyOrder(id);
                        //String orderId=orderService.selectOrderIdById(id);
                        List<OrderCommodityEntity> orderCommodity = orderService.findCidAndNum(orderId);
                        for (OrderCommodityEntity orderCommodit : orderCommodity) {//取消订单库存加上去
                            Integer repertory = (Integer) hos.get("repertory:" + orderCommodit.getCid() + "", "repertory");
                            if (repertory == null) {
                                repertory = shopCarService.findRepertory(orderCommodit.getCid() + "");
                                //Integer volumn=shopCarService.findVolumn(Integer.parseInt(id));
                                //repertory-=volumn;
                                Integer volumn = shopCarService.findVolumn(Integer.parseInt(id));
                                repertory -= volumn;
                            }
                            shopCarService.removeWxUserXg(uuid,orderId,orderCommodit.getCid());
                            repertory += orderCommodit.getNum();
                            hos.put("repertory:" + orderCommodit.getCid() + "", "repertory", repertory);
                        }
                        result.setMessage("取消订单成功");
                    } else {
                        result.setMessage("订单不是本人的，不可以取消");
                        result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
                    }
                }
                retry = false;
            }while (retry);
        if (null != lock) {
            lock.release();
        }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            if (null != lock) {
                try {
                    lock.release();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return result;
    }
    @ApiOperation(value = "订单签收" ,notes = "订单签收",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/signOrder")
    public ApiResult signOrder(String orderId,String uuid){
        ApiResult<Object> result = new ApiResult<>();
        try {
            ValueOperations ops = redisTemplate.opsForValue();
            String ruuid = (String) ops.get("uuid:"+uuid);
            if (uuid==null||!uuid.equals(ruuid)){
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                result.setMessage("用户未登录");
                return result;
            }
            OrderEntity order=orderService.findOrder(orderId,uuid);
            if (order==null){
                result.setMessage("不是本人的订单，不能签收");
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                return result;
            }
            Integer status = order.getStatus();
            if (status==null){
                result.setMessage("不是本人的订单，不能签收");
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                return result;
            }
            if (status!=2&&status!=3){
                result.setMessage("未付款或者没有发货的订单不能签收");
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                return result;
            }
            orderService.signOrders(orderId);
            result.setMessage("订单签收成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
}
