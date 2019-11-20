package com.shopping.service.impl;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.dao.DetailMapper;
import com.shopping.dao.HomePageMapper;
import com.shopping.dao.OrderMapper;
import com.shopping.dao.ShopCarMapper;
import com.shopping.entity.*;
import com.shopping.service.PriceService;
import com.shopping.service.ShopCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ShopCarServiceImpl implements ShopCarService{
    @Autowired
    private ShopCarMapper shopCarMapper;
    @Autowired
    private HomePageMapper homePageMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DetailMapper detailMapper;
    @Autowired
    private Map<String,PriceService> priceServiceMap;
    @Override
    public CommodityEntity findCommodity(String id) {
        ApiResult<Object> result = new ApiResult<>();
        CommodityEntity commodity=shopCarMapper.selectCommodity(id);
        return commodity;
    }

    @Override
    public List<CommodityEntity> findShopCar(Set<String> set,Map<String,Integer> carMap) {
        //把商品id遍历出来，变成字符串
        String m="";
        for (String s:set){
            m+=s+",";
        }
        int i = m.lastIndexOf(",");
        if (i>=0){
            System.out.println(m);
            String ids = m.substring(0, i);
            System.out.println("ids"+ids);
            List<CommodityEntity> commodities = shopCarMapper.selectShopCar(ids);
            //购物车数量放进商品详情
            for (CommodityEntity commodity:commodities){
                System.out.println(commodity.getId());
                CommercialEntity commercial = homePageMapper.selectActiveByCid(commodity.getId());
                commodity.setCommercial(commercial);
                Integer carNum = carMap.get(commodity.getId().toString());
                System.out.println("carNum:"+carNum);
                commodity.setCarNum(carNum);
                //commodities.add(commodity);
            }
            return commodities;
        }
       return null;
    }

    @Override
    public Integer modifyOrderStatus(String orderId) {
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return shopCarMapper.updateOrderStatus(orderId,format);
    }

    @Override
    public Integer findRepertory(String id) {
        return shopCarMapper.selectRepertory(id);
    }

    @Override
    public void modifyRepertory() {
        HashOperations hos = redisTemplate.opsForHash();
        List<Integer> ids=shopCarMapper.selectAllCommodityId();
        for (Integer id:ids){
            Integer repertory = (Integer) hos.get(id, "repertory");
            shopCarMapper.updateRepertory(repertory,id);
        }
    }

    @Override
    @Transactional
    public void modifyOutDateOrders() {
        List<OrderEntity> orders=shopCarMapper.selectCloseTimeAndId();
        HashOperations hos = redisTemplate.opsForHash();
        long now = new Date().getTime();
        for (OrderEntity order:orders){
            if (now-order.getClosetime().getTime()>=0){//该订单过期了
                shopCarMapper.updateOrders(order.getId());
                OrderEntity orderEntity = orderMapper.selectOrderIdById(String.valueOf(order.getId()));
                String uuid = orderEntity.getUid();
                List<OrderCommodityEntity> orderCommodity = orderMapper.selectCidAndNum(orderEntity.getOrderid());
                List<IntegralCommodity> integralCommodityList=orderMapper.selectIntegralCommodity();
                for (OrderCommodityEntity orderCommodit:orderCommodity){//取消订单库存加上去
                    Integer cid = orderCommodit.getCid();//订单中商品的id
                    Integer repertory = (Integer) hos.get(cid+"", "repertory");
                    System.out.println("商品取消时的库存"+repertory);
                    repertory+=orderCommodit.getNum();
                    hos.put(cid+"","repertory",repertory);
                    System.out.println("商品取消后的库存"+hos.get(cid+"","repertory"));
                    for (IntegralCommodity integralCommodity:integralCommodityList){
                        Integer cid1 = integralCommodity.getCid();//积分商品的id
                        Integer integral = integralCommodity.getIntegral();//商品需要的积分
                        if (cid==cid1){
                             orderMapper.updateIntegral(uuid,integral);
                        }
                    }
                }
            }
        }
    }
    //修改redis中库存
    @Override
    public void modifyNum(String orderId) {
        HashOperations hos = redisTemplate.opsForHash();
        List<OrderCommodityEntity> orderCommoditys=shopCarMapper.selectOrderCommodity(orderId);
        for (OrderCommodityEntity orderCommodity:orderCommoditys){
            Integer repertory = (Integer) hos.get(orderCommodity.getCid(), "repertory");
            if (repertory-orderCommodity.getNum()>=0){
                repertory-=orderCommodity.getNum();
            }else {
                repertory=0;
            }
            hos.put(orderCommodity.getCid(),"repertory",repertory);
        }
    }

    @Override
    public OrderEntity findPrice(String orderId) {
        OrderEntity order= shopCarMapper.selectPrice(orderId);
        shopCarMapper.updateScore(order);//将用户积分进行修改
        return order;
    }

    @Override
    public Double findFinalPrice(String orderId) {
        return shopCarMapper.selectFinalPrice(orderId);
    }

    @Override
    public String findOpenid(String uuid) {
        return shopCarMapper.selectOpenid(uuid);
    }

    @Override
    public int addOrderCommodity(OrderCommodityEntity orderCommodity) {
        return shopCarMapper.insertOrderCommodity(orderCommodity);
    }

    @Override
    public List<Integer> findCid(String orderId) {
        return shopCarMapper.selectCid(orderId);
    }

    @Override
    public String findAid(String orderId, Integer id) {
        return shopCarMapper.selectAid(orderId,id);
    }

    @Override
    public String findCAid(Integer id) {
        return shopCarMapper.selectCAid(id);
    }

    @Override
    public void modifyWater() {
       List<CommodityEntity>commodityList=shopCarMapper.selectIdAndRepertory();
       for (CommodityEntity commodity:commodityList){
           Integer id = commodity.getId();
           Integer volumn=shopCarMapper.selectVolumn(id);
           if (volumn==null){
               continue;
           }
           shopCarMapper.updateReper(volumn,id);
       }
       //把商品每天的流水变成0
        shopCarMapper.changeVolumn();
       //把所有签到变成未签到
        shopCarMapper.updateIsSign();
    }

    @Override
    public List<OrderCommodityEntity> findCidAndNum(String orderId) {
        return shopCarMapper.selectCidAndNum(orderId);
    }

    @Override
    public Integer findVolumnId(Integer cid) {
        return shopCarMapper.selectVolumnId(cid);
    }

    @Override
    public void modifyVolumn(OrderCommodityEntity orderCommodity) {
        shopCarMapper.updateVolumn(orderCommodity);
    }

    @Override
    public void addVolumn(OrderCommodityEntity orderCommodity) {
         shopCarMapper.insertVolumn(orderCommodity);
    }

    @Override
    public void retailMoney(String orderId) {
              //查出用户的uuid
        OrderEntity order=shopCarMapper.selectUUIDByOrderId(orderId);
        //查用户的上级
        String uid = order.getUid();
        String superiorid=shopCarMapper.selectSuperByUUID(uid);
        if (superiorid!=null){
            List<OrderCommodityEntity> orderCommodityList=shopCarMapper.selectOrderCommodity(orderId);
            Double totalParentMoney=0.0;
            for (OrderCommodityEntity orderCommodity:orderCommodityList){
                Integer id = orderCommodity.getCid();
                Integer num = orderCommodity.getNum();
                Integer isRetail=shopCarMapper.selectRetail(id);
                if (isRetail==1){//是分销商品
                    RetailEntity retail=shopCarMapper.selectRetailInfo(id);
                    long time = retail.getOuttime().getTime();
                    long now = new Date().getTime();
                    if (now-time<=0){//分销没有过期
                        Integer parenttype = retail.getParenttype();
                        Double parent = retail.getParent();
                        if (parenttype==1){//直接加钱
                             totalParentMoney+=parent*num;
                        }else {//算出优惠总价乘以分销比例
                            String aid=detailMapper.selectActiveById(String.valueOf(id));
                            System.out.println(aid);
                            List<String> actives=detailMapper.selectActiveSortByAid(aid);
                            System.out.println(actives);
                            double totalPrice=0;
                            for (String active:actives){
                                if (priceServiceMap.get(active.trim())!=null) {
                                    totalPrice = priceServiceMap.get(active.trim()).countPrice(String.valueOf(id), num, totalPrice);
                                }
                            }
                            totalParentMoney+=totalPrice*parent;
                        }

                    }else {//分销过期，查看全局分销
                        WholeRetailEntity wholeRetail=shopCarMapper.selectWholeRetail(id);
                        Double wholeparent = wholeRetail.getWholeparent();
                        Integer parenttype = wholeRetail.getParenttype();
                        if (parenttype==1){//直接加钱
                            totalParentMoney+=wholeparent*num;
                        }else {
                            String aid=detailMapper.selectActiveById(String.valueOf(id));
                            System.out.println(aid);
                            List<String> actives=detailMapper.selectActiveSortByAid(aid);
                            System.out.println(actives);
                            double totalPrice=0;
                            for (String active:actives){
                                if (priceServiceMap.get(active.trim())!=null) {
                                    totalPrice = priceServiceMap.get(active.trim()).countPrice(String.valueOf(id), num, totalPrice);
                                }
                            }
                            totalParentMoney+=totalPrice*wholeparent;
                        }
                    }
                }
            }
            if (totalParentMoney!=0){
                shopCarMapper.updateRetailMoney(totalParentMoney,superiorid);
                String wxname=shopCarMapper.selectWxname(uid);
                String content=wxname+"为你获得"+String.format("%.2f",totalParentMoney)+"佣金";
                RetailWater retailWater = new RetailWater();
                retailWater.setOrderid(orderId);
                retailWater.setCreatetime(new Date());
                retailWater.setUuid(superiorid);
                retailWater.setContent(content);
                shopCarMapper.insertRetailWater(retailWater);
                System.out.println(totalParentMoney);
            }

        }else {//没有上级
            return;
        }
        //上上级的分销奖金
        String bigSuperiorid = shopCarMapper.selectSuperByUUID(superiorid);
        if (bigSuperiorid!=null){
            List<OrderCommodityEntity> orderCommodityList=shopCarMapper.selectOrderCommodity(orderId);
            Double totalGrandMoney=0.0;
            for (OrderCommodityEntity orderCommodity:orderCommodityList){
                Integer id = orderCommodity.getCid();
                Integer num = orderCommodity.getNum();
                Integer isRetail=shopCarMapper.selectRetail(id);
                if (isRetail==1){//是分销商品
                    RetailEntity retail=shopCarMapper.selectRetailInfo(id);
                    long time = retail.getOuttime().getTime();
                    long now = new Date().getTime();
                    if (now-time<=0){//分销没有过期
                        Integer grandtype = retail.getGrandtype();
                        Double grand = retail.getGrand();
                        if (grandtype==1){//直接加钱
                            totalGrandMoney+=grand*num;
                        }else {//算出优惠总价乘以分销比例
                            String aid=detailMapper.selectActiveById(String.valueOf(id));
                            System.out.println(aid);
                            List<String> actives=detailMapper.selectActiveSortByAid(aid);
                            System.out.println(actives);
                            double totalPrice=0;
                            for (String active:actives){
                                if (priceServiceMap.get(active.trim())!=null) {
                                    totalPrice = priceServiceMap.get(active.trim()).countPrice(String.valueOf(id), num, totalPrice);
                                }
                            }
                            totalGrandMoney+=totalPrice*grand;
                        }
                    }else {//分销过期，查看全局分销
                        WholeRetailEntity wholeRetail=shopCarMapper.selectWholeRetail(id);
                        Double wholegrand = wholeRetail.getWholegrand();
                        Integer grandtype = wholeRetail.getGrandtype();
                        if (grandtype==1){//直接加钱
                            totalGrandMoney+=wholegrand*num;
                        }else {
                            String aid=detailMapper.selectActiveById(String.valueOf(id));
                            System.out.println(aid);
                            List<String> actives=detailMapper.selectActiveSortByAid(aid);
                            System.out.println(actives);
                            double totalPrice=0;
                            for (String active:actives){
                                if (priceServiceMap.get(active.trim())!=null) {
                                    totalPrice = priceServiceMap.get(active.trim()).countPrice(String.valueOf(id), num, totalPrice);
                                }
                            }
                            totalGrandMoney+=totalPrice*wholegrand;
                        }
                    }
                }
            }
            if (totalGrandMoney!=0){
                shopCarMapper.updateRetailMoney(totalGrandMoney,bigSuperiorid);
                String wxname = shopCarMapper.selectWxname(superiorid);
                String content=wxname+"的下级为你获得"+String.format("%.2f",totalGrandMoney)+"佣金";
                RetailWater retailWater = new RetailWater();
                retailWater.setUuid(bigSuperiorid);
                retailWater.setContent(content);
                retailWater.setOrderid(orderId);
                retailWater.setCreatetime(new Date());
                shopCarMapper.insertRetailWater(retailWater);
            }
        }else {//没有上上级
            return;
        }
    }

    @Override
    public CommodityEntity findIsintegralAndBeretail(Integer cid) {
        return shopCarMapper.selectIsintegralAndBeretail(cid);
    }

    @Override
    public String findUid(String orderId) {
        return shopCarMapper.selectUid(orderId);
    }

    @Override
    public void modifyBeRetail(String uid) {
        shopCarMapper.updateBeRetail(uid);
    }
}
