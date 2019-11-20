package com.shopping.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
public class PrePayDemo {
	
	public static Object posPrePayRe(PosPrepay posPrePay) {
		String access_token="48a7a2629bb5471b8d4bed7b092022e5";// //d72cdf8a78de42cbbab31de9699775dd
		String prePay_url="https://pay.lcsw.cn/lcsw/pay/100/minipay";// //http://test.lcsw.cn:8045/lcsw/pay/100/minipay
		//PosPrepayRe posPrePayRe = new PosPrepayRe();
		try {
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("pay_ver", posPrePay.getPay_ver());
			jsonParam.put("pay_type", posPrePay.getPay_type());
			jsonParam.put("service_id", posPrePay.getService_id());
			jsonParam.put("merchant_no", posPrePay.getMerchant_no());
			jsonParam.put("terminal_id", posPrePay.getTerminal_id());
			jsonParam.put("terminal_trace", posPrePay.getTerminal_trace());
			jsonParam.put("terminal_time", posPrePay.getTerminal_time());
			jsonParam.put("total_fee", 1);
			jsonParam.put("order_body", posPrePay.getOrder_body());
			jsonParam.put("notify_url",posPrePay.getNotify_url());
			jsonParam.put("open_id",posPrePay.getOperator_id());
			jsonParam.put("sub_appid","wxa49b35cbb6b4708e");
			String parm = "pay_ver=" + posPrePay.getPay_ver() + "&pay_type=" + posPrePay.getPay_type() + "&service_id="
					+ posPrePay.getService_id() + "&merchant_no=" + posPrePay.getMerchant_no() + "&terminal_id="
					+ posPrePay.getTerminal_id() + "&terminal_trace=" + posPrePay.getTerminal_trace()
					+ "&terminal_time=" + posPrePay.getTerminal_time() + "&total_fee=" + 1
					// +"&order_body="+posPrePay.getOrder_body()
					+ "&access_token=" +access_token;
			String sign = MD5.sign(parm, "utf-8");
			jsonParam.put("key_sign", sign);
			System.out.println(prePay_url + "");
			String xmlText = tojson(prePay_url, jsonParam.toJSONString());
			Object aaa = JSON.parse(xmlText);
			return  aaa;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


    public static String tojson(String gateway,String jsonParam)throws Exception {
		
		String xmlText = "";
		
		CloseableHttpClient httpclient = HttpClients.custom().build();
		try {
			
			 HttpPost httpPost = new HttpPost(gateway);
             httpPost.addHeader("charset", "UTF-8");
             System.out.println(jsonParam.toString());
			 StringEntity stentity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题    
			 stentity.setContentEncoding("UTF-8");    
			 stentity.setContentType("application/json");
			 httpPost.setEntity(stentity);
		     CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
					String text;
					while ((text = bufferedReader.readLine()) != null) {
						xmlText = xmlText + text;
					}
				}
				EntityUtils.consume(entity);
				System.out.println(xmlText);
			} finally {
				
				response.close();
			}
		} finally {
			
			httpclient.close();
	    }
		
	    return xmlText;
 }

		
		
		
	
}
