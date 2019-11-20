package com.shopping.util;


import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 系统参数
 */
@Component
@Data
public class Parameters {

    private String redisHost = "193.112.60.165";

    private int redisPort = 6379;

    private String redisAuth;

    private int redisMaxTotal = 5;

    private int redisMaxIdle = 10;

    private int redisMaxWaitMillis = 3000;

    private String zkHost = "106.14.195.35:2181";

    private String esHost="193.112.60.165:9200";


}