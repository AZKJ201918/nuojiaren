package com.shopping.controller;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.commons.sm.sendSmsUtil;
import com.shopping.entity.RegisterEntity;
import com.shopping.entity.RetailWater;
import com.shopping.entity.WxUser;
import com.shopping.service.RegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "注册模块")
public class RegisterController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "获取验证码",notes = "获取验证码",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/getSmsCode")
    public ApiResult getSmsCode(String phone){
        ApiResult<Object> result = new ApiResult<>();
        int code= (int) (Math.random()*900000+100000);
        ValueOperations ops = redisTemplate.opsForValue();
        try {
            sendSmsUtil.execute(phone,code+"");
            ops.set("phone:"+phone,code+"",5L, TimeUnit.MINUTES);
            result.setMessage("验证码发送正常");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "是否注册",notes = "是否注册",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/isRegister")
    public ApiResult isRegister(String uuid){
        ApiResult<Object> result = new ApiResult<>();
        try {
            String name=registerService.findRegisterByPhone(uuid);
            if (name!=null){
                result.setMessage("已经注册过");
                result.setData(1);
            }else {
                result.setMessage("用户未注册");
                result.setData(2);
            }
        } catch (Exception e) {
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            e.printStackTrace();
        }
        return result;
    }
    @ApiOperation(value = "注册",notes = "注册",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/register")
    public ApiResult register(RegisterEntity registerEntity){
        ApiResult<Object> result = new ApiResult<>();
        ValueOperations ops = redisTemplate.opsForValue();
        String code = registerEntity.getCode();
        String phone = registerEntity.getPhone();
        String code1 = (String) ops.get("phone:"+phone);
        if (code==null||!code.equals(code1)){
            result.setMessage("验证码输入错误");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            return result;
        }
        if (code1==null){
            result.setMessage("您还没有发送验证码");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            return result;
        }
        try {
            String bankid = registerEntity.getBankid();
            //查看是否存在默认银行卡
            String mrBankID=registerService.findMrBankId(registerEntity.getUuid());
            if (mrBankID==null){
                registerService.addMrBankID(bankid,registerEntity.getUuid());
            }else {
                String uuid = registerService.findBankId(bankid);
                if (uuid == null) {
                    registerService.addBankId(bankid, registerEntity.getUuid());
                }
            }
            registerService.addRegister(registerEntity);
            result.setMessage("注册成功");
        } catch (Exception e) {
            result.setMessage("服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            e.printStackTrace();
        }
        return result;
    }
    @ApiOperation(value = "签到",notes = "签到",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/sign")
    public ApiResult sign(String uuid){
        ApiResult<Object> result = new ApiResult<>();
        try {
            Integer isSign=registerService.findIsSign(uuid);
            if (isSign==0){
                Integer integral=registerService.findIntergral();
                System.out.println(integral);
                registerService.modifyIntegralAndIsSign(uuid,integral);
                result.setMessage("签到成功");
                return result;
            }else {
                result.setMessage("你已签到");
                result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "是否签到",notes = "是否签到",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/isSign")
    public ApiResult isSign(String uuid){
        ApiResult<Object> result = new ApiResult<>();
        Integer isSign=registerService.findIsSign(uuid);
        if (isSign==0){
            result.setMessage("你还没有签到");
            result.setData(0);
            return result;
        }else {
            result.setMessage("你已签到");
            result.setData(1);
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看分销所得金额明细",notes = "查看下级以及下下级分销",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadRetail")
    public ApiResult loadRetail(String uuid,Integer page){
        ApiResult<Object> result = new ApiResult<>();
        try {
            List<RetailWater> retailWaterList=registerService.findRetailWater(uuid,page);
            result.setMessage("查看成功");
            result.setData(retailWaterList);
        } catch (SuperMarketException e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看佣金和积分",notes = "查看佣金和积分",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadScore")
    public ApiResult loadScore(String uuid){
        ApiResult<Object> result = new ApiResult<>();
        try {
            WxUser wxUser=registerService.findIntegralAndRetailMoney(uuid);
            result.setMessage("查看积分佣金成功");
            result.setData(wxUser);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "查看下级",notes = "查看下级",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/loadNext")
    public ApiResult loadNext(String uuid,Integer page){
        ApiResult<Object> result = new ApiResult<>();
        try {
            List<WxUser> wxUserList=registerService.findNext(uuid,page);
            result.setMessage("查看成功");
            result.setData(wxUserList);
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
    @ApiOperation(value = "查看是否是分销商",notes = "查看是否是分销商",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/isRetail")
    public ApiResult isRetail(String uuid){
        ApiResult<Object> result = new ApiResult<>();
        try {
            Map<String,Object> map=registerService.findIsRetail(uuid);
            result.setMessage("查看成功,true表示是分销商，false不是分销商");
            result.setData(map);
        } catch (SuperMarketException e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
}
