package com.shopping.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlUtil {
    //解析xml
    public static  Map<String, Object> getPrepayMapInfo(String Str)  {
        //解析并读取统一下单中的参数信息
        //1.去掉前后的xml标签
        String notityXml = Str.replaceAll("</?xml>", "");
        System.out.println(notityXml);
        //2.匹配一段一段这样的数据   <attach><![CDATA[支付测试]]></attach>
        Pattern pattern = Pattern.compile("<.*?/.*?>");
        Matcher matcher = pattern.matcher(notityXml);
        //3.配置是否包含<![CDATA[CNY]]> CDATA 包裹的数据
        Pattern pattern2 = Pattern.compile("!.*]");
        Map<String, Object> mapInfo = new HashMap<>();
        while(matcher.find()) {
            //获取键
            String key = matcher.group().replaceAll(".*/", "");
            key = key.substring(0, key.length() - 1);
            Matcher matcher2 = pattern2.matcher(matcher.group());
            String value = matcher.group().replaceAll("</?.*?>", "");
            //获取值
            if(matcher2.find() && !value.equals("DATA")) {
                value = matcher2.group().replaceAll("!.*\\[", "");
                value = value.substring(0, value.length() - 2);
            }
            mapInfo.put(key, value);
        }
        return mapInfo;
    }

}
