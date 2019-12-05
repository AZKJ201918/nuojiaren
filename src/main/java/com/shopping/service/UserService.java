package com.shopping.service;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.AddressEntity;
import com.shopping.entity.Cash;

import java.util.List;

public interface UserService {
    String findMrAddress(String uid);

    int addAddress(AddressEntity address);

    List<AddressEntity> findAllAddress(String uid) throws SuperMarketException;

    int modifyAddress(AddressEntity address);

    int scAddress(String id);

    int changeAddressStatus(String id);

    int changeMrAddress(String uid);

    void modifyCash(Integer money, String uuid) throws SuperMarketException;

    List<Cash> findCash(String uuid);
}
