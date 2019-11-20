package com.shopping.service;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.RegisterEntity;
import com.shopping.entity.RetailWater;
import com.shopping.entity.WxUser;

import java.util.List;
import java.util.Map;

public interface RegisterService {
    String findRegisterByPhone(String uuid);

    int addRegister(RegisterEntity registerEntity);

    String findMrBankId(String uuid);

    int addMrBankID(String bankid,String uuid);

    String findBankId(String bankid);

    int addBankId(String bankid,String uuid);

    int modifyIntegralAndIsSign(String uuid,Integer integral);

    Integer findIsSign(String uuid);

    Integer findIntergral();

    List<RetailWater> findRetailWater(String uuid,Integer page) throws SuperMarketException;

    WxUser findIntegralAndRetailMoney(String uuid);

    List<WxUser> findNext(String uuid, Integer page) throws SuperMarketException;

    Map<String, Object> findIsRetail(String uuid) throws SuperMarketException;
}
