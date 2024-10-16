package com.maan.eway.integration.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.integration.req.PremiaListRequest;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.IntegrationService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
 

@RestController
@RequestMapping("/push/integration")
@Api(tags = "Integeraion Controller : Premia Integration ", description = "API's")
public class IntegrationController {

	@Autowired
	private IntegrationService service;
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@PostMapping("/quote")
	public ResponseEntity<CommonRes> pushPremiaIntegeration(@RequestBody PremiaRequest req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = service.pushPremiaIntegration(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/hitByQuoteNo")
	public ResponseEntity<CommonRes> hitByQuoteNo(@RequestBody PremiaListRequest req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = service.hitByQuoteNo(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
}
