package com.shopping.commons.quartz;
import com.shopping.controller.HomePageController;
import com.shopping.service.ShopCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableScheduling
public class QuartzTask {
    @Autowired
    private ShopCarService shopCarService;
    @Autowired
    private HomePageController homePageController;
    @Scheduled(cron = "0 0 */2 * * ?")
    //@Scheduled(cron = "*/5 * * * * ?")
    //定时取消过期的订单
    public void play() throws Exception {
         shopCarService.modifyOutDateOrders();
         System.out.println("执行完成1");
     }


    //每天24点刷一次
    //修改数据库的库存，减去流水
    //把所有的签到信息改为未签到
    //@Scheduled(cron = "*/5 * * * * ?")
    @Scheduled(cron = "0 0 0 * * ?")
    public void QuartzTask(){
       /* shopCarService.modifyRepertory();
        System.out.println("执行完成2");*/
        System.out.println("执行任务2");
        shopCarService.modifyWater();
        Map<String, Integer> map = homePageController.visitVolume();
        if (map!=null){
            shopCarService.addRecord(map);
        }
        homePageController.zeroToday();
    }

}


