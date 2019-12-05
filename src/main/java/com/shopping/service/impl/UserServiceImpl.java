package com.shopping.service.impl;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.UserMapper;
import com.shopping.entity.AddressEntity;
import com.shopping.entity.Cash;
import com.shopping.entity.WxUser;
import com.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public String findMrAddress(String uid) {
        return userMapper.selectMrAddress(uid);
    }

    @Override
    public int addAddress(AddressEntity address) {
        return userMapper.insertAddress(address);
    }

    @Override
    public List<AddressEntity> findAllAddress(String uid) throws SuperMarketException {
        return userMapper.selectAllAddress(uid);
    }

    @Override
    public int modifyAddress(AddressEntity address) {
        return userMapper.updateAddress(address);
    }

    @Override
    public int scAddress(String id) {
        return userMapper.deleteAddress(id);
    }

    @Override
    public int changeAddressStatus(String id) {
        return userMapper.updateAddressStatus(id);
    }

    @Override
    public int changeMrAddress(String uid) {
        return userMapper.updateMrAddress(uid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCash(Integer money, String uuid) throws SuperMarketException {
        WxUser wxUser=userMapper.selectRetailMoneyByUUID(uuid);
        if (wxUser.getRetailMoney()==null){
            throw new SuperMarketException("没有钱包信息");
        }
        if (wxUser.getMoney()>0){
            throw new SuperMarketException("你有审批正在路上，不能重复提现");
        }
        if (money>wxUser.getRetailMoney()){
            throw new SuperMarketException("兑换金额大于钱包余额");
        }
        Date date = new Date();
        userMapper.insertCash(money,date,uuid);
        userMapper.updateMoneyByUUID(uuid,money);
    }

    @Override
    public List<Cash> findCash(String uuid) {
        return userMapper.selectCash(uuid);
    }


}
