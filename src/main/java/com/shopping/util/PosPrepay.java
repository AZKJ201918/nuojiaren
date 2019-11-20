package com.shopping.util;

import java.util.Date;

/**
 * 预支付实体
 * @author liuchu-zy
 *
 */
public class PosPrepay {
	
	private String pay_ver="100";//版本号
	
	private String pay_type="010";//请求类型
	
	private String service_id="015";//接口类型
	
	private String merchant_no="830505977000003";//商户号 //816400164000005
	
	private String terminal_id="11429077";//终端号 //30056226


	private String terminal_trace;//终端流水号
	
	private String terminal_time;//终端交易时间

	private String notify_url="https://www.azwst.co/nuojiaren/annocy";
	
	private Integer total_fee;//金额
	
	private String operator_id;//操作员号
	
	private String order_body;//订单描述
	
	private String key_sign; //签名检验串

	public String getPay_ver() {
		return pay_ver;
	}

	public void setPay_ver(String pay_ver) {
		this.pay_ver = pay_ver;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getMerchant_no() {
		return merchant_no;
	}

	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getTerminal_trace() {
		return terminal_trace;
	}

	public void setTerminal_trace(String terminal_trace) {
		this.terminal_trace = terminal_trace;
	}

	public String getTerminal_time() {
		return terminal_time;
	}

	public void setTerminal_time(String terminal_time) {
		this.terminal_time = terminal_time;
	}


	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOrder_body() {
		return order_body;
	}

	public void setOrder_body(String order_body) {
		this.order_body = order_body;
	}

	public String getKey_sign() {
		return key_sign;
	}

	public void setKey_sign(String key_sign) {
		this.key_sign = key_sign;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public Integer getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(Integer total_fee) {
		this.total_fee = total_fee;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
}
