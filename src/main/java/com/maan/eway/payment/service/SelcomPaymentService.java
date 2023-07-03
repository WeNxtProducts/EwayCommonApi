package com.maan.eway.payment.service;

import com.google.gson.JsonObject;

public interface SelcomPaymentService {

	JsonObject createOrderForPayment(String merchantRefernceNo);

	JsonObject methodWebhook(JsonObject jsObject);

	JsonObject orderStatus(String orderId);

}
