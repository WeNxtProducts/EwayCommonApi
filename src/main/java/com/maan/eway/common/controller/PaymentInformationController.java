package com.maan.eway.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.PaymentInformationGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PaymentInformationGetRes;
import com.maan.eway.common.service.PaymentInformationService;

@RestController
@RequestMapping("/payment")

public class PaymentInformationController {

	@Autowired
    private  PaymentInformationService paymentinfoservice;
	
	// Get By Payment Id
	@PostMapping("/getbypaymentinfoid")
	public ResponseEntity<CommonRes> getByPaymentId(@RequestBody PaymentInformationGetReq req) {
		CommonRes data = new CommonRes();
//		List<PaymentInformationGetRes> paymentgetres = paymentinfoservice.getByPaymentInformationId(
//				req);
		
		
		List<PaymentInformationGetRes> paymentgetres=new ArrayList<PaymentInformationGetRes>();
		paymentgetres=paymentinfoservice.getByPaymentInformationId(req);
		
//		Stream<List<PaymentInformationGetRes>> paymentgetres = Stream.of(paymentinfoservice.getByPaymentInformationId(
//				
//				req));

		
		
		
		data.setCommonResponse(paymentgetres);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");

		if (paymentgetres != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		
	}
	

}
