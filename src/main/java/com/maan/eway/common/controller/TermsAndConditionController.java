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

import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.service.TermsAndConditionService;
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
		
		List<TermsAndConditionRes> res = service.viewTermsAndCondition(req);
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
