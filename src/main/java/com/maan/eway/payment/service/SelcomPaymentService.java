package com.maan.eway.payment.service;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;


public interface SelcomPaymentService {

	JsonObject createOrderForPayment(String merchantRefernceNo);

	JsonObject methodWebhook(JsonObject jsObject);

	JsonObject orderStatus(String orderId);

}
