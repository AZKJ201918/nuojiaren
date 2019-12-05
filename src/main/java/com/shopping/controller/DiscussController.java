package com.shopping.controller;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.shopping.commons.constans.Constants;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.Discuss;
import com.shopping.entity.WxUser;
import com.shopping.service.HomePageService;
import com.shopping.service.OrderService;
import com.shopping.util.QiniuFileUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "评论模块")
public class DiscussController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HomePageService homePageService;
    @Autowired
    private  OrderService orderService;

    @ApiOperation(value = "新增商品评论",notes = "商品评论",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/insertDiscuss")
    public ApiResult insertDiscuss(String discusses, Integer id, String uuid){
        List<Discuss> discussList = JSON.parseArray(discusses,Discuss.class);
        System.out.println(discussList);
        ApiResult<Object> result = new ApiResult<>();
        Integer status=orderService.findStatus(id);
        if (status==5){
            result.setMessage("订单已完成评价");
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
            return result;
        }
        WxUser wxUser=homePageService.findwxnameAndHeadimgurl(uuid);
        try {
            for (Discuss discuss:discussList){
                discuss.setWxname(wxUser.getNickname());
                discuss.setWximg(wxUser.getHeadimgurl());
                discuss.setPlusDetails("");
                mongoTemplate.save(discuss);
            }
            orderService.updateStatus(id);
            result.setMessage("评论新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("评论新增失败");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看商品评论",notes = "商品评论",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadDiscuss")
    public ApiResult loadDiscuss(Integer id,Integer page,String evaluate){
        ApiResult<Object> result = new ApiResult<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("cid").is(id));
        if (evaluate!=null){
            query.addCriteria(Criteria.where("evaluate").is(evaluate));
        }
        List<Discuss> discusses = null;
        try {
            long count = mongoTemplate.count(query, Discuss.class);
            query.skip((page-1)*10).limit(10);
            discusses = mongoTemplate.find(query, Discuss.class);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("discusses",discusses);
            map.put("count",count);
            System.out.println(discusses);
            result.setMessage("查看评论成功");
            result.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "上传评论图片",notes = "评论图片",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/discussImg")
    public ApiResult discussImg(MultipartFile file){
        ApiResult<Object> result = new ApiResult<>();
        try {
            String detailsImg = QiniuFileUploadUtil.uploadHeadImg(file);
            result.setMessage("文件上传成功");
            result.setData(detailsImg);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "追加评论",notes = "追加评论",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/plusDiscuss")
    public ApiResult deleteDiscuss(String plusDiscusses,String orderId){
        ApiResult<Object> result = new ApiResult<>();
        System.out.println(plusDiscusses);
        List<Discuss> plusDisscussList = JSON.parseArray(plusDiscusses, Discuss.class);
        System.out.println(plusDisscussList);
        for (Discuss discuss:plusDisscussList){
            Query query = new Query();
            System.out.println(discuss.getId());
            query.addCriteria(Criteria.where("cid").is(discuss.getCid()));
            query.addCriteria(Criteria.where("uid").is(discuss.getUid()));
            query.addCriteria(Criteria.where("orderId").is(orderId));
            Update update = Update.update("plusDetails",discuss.getPlusDetails());
          //  update.set("plusDetails",discuss.getPlusDetails());
            try {
                UpdateResult upsert = mongoTemplate.updateFirst(query,update,Discuss.class);
                if (upsert.getMatchedCount()>0){
                    result.setMessage("追加评论成功");
                    result.setData(1);
                }else {
                    result.setMessage("追加失败");
                    result.setData(0);
                    result.setCode(Constants.RESP_STATUS_BADREQUEST);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessage("后台服务器异常");
                result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            }
        }
        return result;
    }

}
