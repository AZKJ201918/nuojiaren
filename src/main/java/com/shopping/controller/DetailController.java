package com.shopping.controller;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.CommercialEntity;
import com.shopping.entity.CommodityEntity;
import com.shopping.service.DetailService;
import com.shopping.service.PriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "商品详情模块")
public class DetailController {

    @Autowired
    private DetailService detailService;
    @Autowired
    private Map<String,PriceService> priceServiceMap;


    @ApiOperation(value = "商品详情",notes = "商品详情",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/detail")
    public ApiResult<Map<String,Object>> detail(String id){
        ApiResult<Map<String,Object>> result = new ApiResult<>();
        try {
            List<CommodityEntity>detailList=detailService.findDetailById(id);
            List<String> detailBannerList=detailService.findDetailBannerById(id);
            HashMap<String, Object> map = new HashMap<>();
            map.put("detailList",detailList);
            map.put("detailBannerList",detailBannerList);
            result.setMessage("商品详情查看成功");
            result.setData(map);
        } catch (Exception e) {
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("服务器异常");
            e.printStackTrace();
        }
        return result;
    }
    @ApiOperation(value = "价格结算",notes = "价格结算",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/countPrice")
    public ApiResult countPrice(String id,Integer num,String uuid){
        ApiResult<Object> result = new ApiResult<>();
        CommercialEntity commercial=detailService.findActiveById(id);
        String aid = commercial.getAid();
        System.out.println(aid);
        List<String> actives=detailService.findActiveSortByAid(aid);
        System.out.println(actives);
        double totalPrice=0;
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
        return result;
    }
}
