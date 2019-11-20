package com.shopping.commons.sm;

import java.net.URLEncoder;

public class sendSmsUtil {

    /**
     * 短信发送(验证码通知，会员营销)
     * 接口文档地址：http://www.miaodiyun.com/doc/https_sms.html
     */
    public static void execute(String phone,String code) throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append("accountSid").append("=").append(Config.ACCOUNT_SID);
        sb.append("&to").append("=").append(phone);//"17683716034"
        sb.append("&param").append("=").append(URLEncoder.encode(code,"UTF-8"));//"123456"
        sb.append("&templateid").append("=").append("217017");
//		sb.append("&smsContent").append("=").append( URLEncoder.encode("【秒嘀科技】您的验证码为123456，该验证码5分钟内有效。请勿泄漏于他人。","UTF-8"));
        String body = sb.toString() + HttpUtil.createCommonParam(Config.ACCOUNT_SID, Config.AUTH_TOKEN);
        String result = HttpUtil.post(Config.BASE_URL, body);
        System.out.println(result);

    }

}
