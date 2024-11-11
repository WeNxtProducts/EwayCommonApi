package com.maan.eway.payment.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;


public interface SelcomPaymentService {

	JsonObject createOrderForPayment(String merchantRefernceNo);

	JsonObject methodWebhook(Map<String,Object> jsObject);

	JsonObject orderStatus(String orderId, String tokens);
	JsonObject createOrderForPayment(PaymentDetail payment); 
	
	JsonObject createOrderMinimal(String merchantRefernceNo);
}
