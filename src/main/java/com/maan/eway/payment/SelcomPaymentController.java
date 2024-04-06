package com.maan.eway.payment;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.service.SelcomPaymentService;

import io.swagger.annotations.ApiOperation;




@RestController
@RequestMapping("/selcom")
public class SelcomPaymentController {
	@Autowired
	private SelcomPaymentService service;
	
	@PostMapping("/v1/checkout/create-order/{merchantRefernceNo}")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<JsonObject> createOrder(@RequestParam String merchantRefernceNo) {
		
		JsonObject data =service.createOrderForPayment(merchantRefernceNo);
		if (data != null) {
			return new ResponseEntity<JsonObject>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}	
	}
	
	
	@PostMapping("/v1/checkout/webhook")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<Map<String,Object>> methodWebhook(@RequestBody Map<String,Object> jsObject) {
		/*new Runnable() {
			
			@Override
			public void run() {
				
			}
		};*/
		service.methodWebhook(jsObject);
		jsObject.put("AcknowledegeStatus", true);
		return new ResponseEntity<Map<String,Object>>(jsObject, HttpStatus.CREATED);	
	}
	
	@PostMapping("/v1/checkout/order-status/{orderId}")
	@ApiOperation(value = "This method is to Payment ") 
	public ResponseEntity<Object> orderStatus(@PathVariable("orderId") String orderId,@RequestHeader("Authorization") String tokens) {
		JsonObject data = service.orderStatus(orderId,tokens);
		if (data != null) {
			return new ResponseEntity<Object>(data.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/v1/checkout/create-order-minim/{merchantRefernceNo}")
	@ApiOperation(value = "This method is to Payment Sava")
	//@RequestBody
	public ResponseEntity<Object> createOrderMinimum(@PathVariable("merchantRefernceNo") String merchantRefernceNo) {
		
		JsonObject data =service.createOrderMinimal(merchantRefernceNo);
		if (data != null) {
			return new ResponseEntity<Object>(data.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}	
	}
	
}
