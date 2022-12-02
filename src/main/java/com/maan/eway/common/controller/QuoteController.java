package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.error.Error;
import com.maan.eway.res.QuoteUpdateRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/quote")
public class QuoteController {

	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private  QuoteService entityService ;
	
	@PostMapping("/buypolicy")
	@ApiOperation(value = "This method is New Quote ")
	public ResponseEntity<CommonRes> generateNewQuote(@RequestBody NewQuoteReq req) {

		reqPrinter.reqPrint(req);
		// Save
		CommonRes res = entityService.generateNewQuote(req);
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	
	@PostMapping("/viewquotedetails")
	@ApiOperation(value = "This method is Get Quote Details")
	public ResponseEntity<CommonRes> viewQuoteDetails(@RequestBody ViewQuoteReq req) {
		CommonRes commonRes = new  CommonRes() ;
		reqPrinter.reqPrint(req);
		
		// Save
		ViewQuoteRes res = entityService.viewQuoteDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	} 
	
	
	@PostMapping("/update/referalstatus")
	public ResponseEntity<CommonRes> saveCustomerDetails(@RequestBody  AdminReferalStatusReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validateReferralStatus(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			QuoteUpdateRes res = entityService.updateReferralStatus(req);
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
}
