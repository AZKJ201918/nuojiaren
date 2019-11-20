package com.shopping.service;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.AddressEntity;

import java.util.List;

public interface UserService {
    String findMrAddress(String uid);

    int addAddress(AddressEntity address);

    List<AddressEntity> findAllAddress(String uid) throws SuperMarketException;

    int modifyAddress(AddressEntity address);

    int scAddress(String id);

    int changeAddressStatus(String id);

    int changeMrAddress(String uid);
}
