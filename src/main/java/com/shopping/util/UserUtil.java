package com.shopping.util;



import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.UserMapper;
import com.shopping.entity.WxUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public  class UserUtil {

    @Autowired
    private UserMapper userMapper;




   public  void UserElent(Integer uid) throws SuperMarketException {
       if(uid==0){
           throw  new SuperMarketException("请登录");
       }
       if(uid==null){
           throw  new SuperMarketException("请登录");
       }
       WxUser user=userMapper.findByPrimaryKey(uid);
       if(user==null) {
           throw new SuperMarketException("请登录");
       }


   }

}
