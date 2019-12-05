package com.shopping.controller;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.AddressEntity;
import com.shopping.entity.Cash;
import com.shopping.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "用户模块")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "新增收货地址",notes = "新增收货地址",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/insertAddress")
    public ApiResult insertAddress(AddressEntity address){
        ApiResult<Object> result = new ApiResult<>();
        try {
            String id=userService.findMrAddress(address.getUid());
            if (id==null){//用户没有默认地址
                address.setStatus(0);
                userService.addAddress(address);
                result.setMessage("用户地址新增成功，设为默认地址");
                result.setData(1);
                return result;
            }
            address.setStatus(1);
            userService.addAddress(address);
            result.setMessage("用户地址新增成功，未设为默认地址");
            result.setData(2);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台未知错误");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看收货地址",notes = "查看收货地址",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/selectAddress")
    public ApiResult<List<AddressEntity>> selectAddress(String uid){
        ApiResult<List<AddressEntity>> result=new ApiResult<>();
        ValueOperations ops = redisTemplate.opsForValue();
        try {
            String ruuid = (String) ops.get("uuid:"+uid);
            if (uid==null||ruuid==null||!uid.equals(ruuid)){
                result.setCode(Constants.RESP_STATUS_BADREQUEST);
                result.setMessage("用户未登录");
                return result;
            }
            List<AddressEntity> addressList= userService.findAllAddress(uid);
            result.setMessage("查看收货地址成功");
            result.setData(addressList);
        } catch (SuperMarketException e) {
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("后台错误");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "修改收货地址",notes = "修改收货地址",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/updateAddress")
    public ApiResult updateAddress(AddressEntity address){
        ApiResult result=new ApiResult();
        try {
            userService.modifyAddress(address);
            result.setMessage("用户修改地址成功");
        } catch (Exception e) {
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("服务器异常");
            e.printStackTrace();
        }
        return result;
    }
    @ApiOperation(value = "删除收货地址",notes = "删除收货地址",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/deleteAddress")
    public ApiResult deleteAddress(String id){
        ApiResult result=new ApiResult();
        try {
            userService.scAddress(id);
            result.setMessage("删除地址成功");
        } catch (Exception e) {
            result.setMessage("后台错误，删除失败");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            e.printStackTrace();
        }
        return result;
    }
    @ApiOperation(value = "修改为默认地址",notes = "修改为默认地址",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/changeAddressStatus")
    @Transactional
    public ApiResult changeAddressStatus(String id,String uid){
        ApiResult result=new ApiResult();
        try {
            userService.changeMrAddress(uid);//将用户的已存默认地址修改为非默认
            userService.changeAddressStatus(id);//将用户的现地址修改为默认
            result.setMessage("修改默认地址成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "申请提现",notes = "申请提现",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/cash")
    public ApiResult cash(Integer money,String uuid){
        ApiResult result=new ApiResult();
        try {
           userService.modifyCash(money,uuid);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看申请记录",notes = "查看申请记录",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/record")
    public ApiResult record(String uuid){
        ApiResult result=new ApiResult();
        try {
            List<Cash> cashList =userService.findCash(uuid);
            result.setMessage("查看申请记录成功");
            result.setData(cashList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
}
