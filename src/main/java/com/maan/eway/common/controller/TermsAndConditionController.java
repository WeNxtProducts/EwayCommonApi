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

import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.service.TermsAndConditionService;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Terms And Condition", description="API's")
@RequestMapping("/api")
public class TermsAndConditionController {

@Autowired
private TermsAndConditionService service;

@Autowired
private PrintReqService reqPrinter;


//  View Terms And Condition

	@PostMapping("/viewtermsandcondition")
	@ApiOperation("This method is View Terms And Condition")
	public ResponseEntity<CommonRes> viewTermsAndCondition(@RequestBody TermsAndConditionReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionRes res = service.viewTermsAndCondition(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}

	
	// Insert Terms And Condition
	

	@PostMapping("/inserttermsandcondition")
	@ApiOperation("This method is Insert Terms And Condition")
	public ResponseEntity<CommonRes> insertTermsAndCondition(@RequestBody TermsAndConditionInsertReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		

		List<Error> validation = service.validateTermsAndCondition(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
		SuccessRes res = service.insertTermsAndCondition(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}
	
	}
	
	

//  Get Terms And Condition

	@PostMapping("/gettermsandcondition")
	@ApiOperation("This method is Get Terms And Condition")
	public ResponseEntity<CommonRes> getTermsAndCondition(@RequestBody TermsAndConditionGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionGetRes res = service.getTermsAndCondition(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}


//  Get Terms And Condition By Subid

	@PostMapping("/gettermsandconditionbysubid")
	@ApiOperation("This method is Get Terms And Condition by Sub Id")
	public ResponseEntity<CommonRes> getTermsAndConditionSubId(@RequestBody TermsAndConditionGetBySubIdReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionGetBySubIdRes res = service.getTermsAndConditionSubId(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}

	
}
