package com.shopping.service.impl;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.UserMapper;
import com.shopping.entity.AddressEntity;
import com.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
