package com.shopping.service.impl;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.RegisterMapper;
import com.shopping.entity.CommodityEntity;
import com.shopping.entity.RegisterEntity;
import com.shopping.entity.RetailWater;
import com.shopping.entity.WxUser;
import com.shopping.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private RegisterMapper registerMapper;
    @Override
    public String findRegisterByPhone(String uuid) {
        return registerMapper.selectRegisterByPhone(uuid);
    }

    @Override
    public int addRegister(RegisterEntity registerEntity) {
        return registerMapper.insertRegister(registerEntity);
    }

    @Override
    public String findMrBankId(String uuid) {
        return registerMapper.selectMrBankId(uuid);
    }

    @Override
    public int addMrBankID(String bankid,String uuid) {
        return registerMapper.insertMrBankId(bankid,uuid);
    }

    @Override
    public String findBankId(String bankid) {
        return registerMapper.selectBankId(bankid);
    }

    @Override
    public int addBankId(String bankid,String uuid) {
        return registerMapper.insertBankId(bankid,uuid);
    }

    @Override
    public int modifyIntegralAndIsSign(String uuid,Integer integral) {
        return registerMapper.updateIntegralAndIsSign(uuid,integral);
    }

    @Override
    public Integer findIsSign(String uuid) {
        return registerMapper.selectIsSign(uuid);
    }

    @Override
    public Integer findIntergral() {
        return registerMapper.selectIntergral();
    }

    @Override
    public List<RetailWater> findRetailWater(String uuid, Integer page) throws SuperMarketException{
        Integer start=(page-1)*10;
        List<RetailWater> retailWaterList = registerMapper.selectRetailWater(uuid, start);
        if (retailWaterList==null){
            throw new SuperMarketException("没有查到分销信息");
        }
        return retailWaterList;
    }

    @Override
    public WxUser findIntegralAndRetailMoney(String uuid) {
        return registerMapper.selectIntegralAndRetailMoney(uuid);
    }

    @Override
    public List<WxUser> findNext(String uuid, Integer page) throws SuperMarketException {
        Integer start=(page-1)*10;
        List<WxUser> wxUserList = registerMapper.selectNext(uuid, start);
        if (wxUserList==null){
            throw new SuperMarketException("您还没有下级");
        }
        return wxUserList;
    }

    @Override
    public Map<String, Object> findIsRetail(String uuid) throws SuperMarketException {
        Map map = new HashMap<>();
        Integer isRetail=registerMapper.selectIsRetail(uuid);
        List<CommodityEntity> commodity=registerMapper.selectBeRetail();
        if (isRetail==null){
            throw new SuperMarketException("没有这个用户");
        }
        map.put("isRetail",isRetail);
        map.put("commodity",commodity);
        return map;
    }


}
