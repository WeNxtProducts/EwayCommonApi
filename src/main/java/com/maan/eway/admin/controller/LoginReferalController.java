package com.maan.eway.admin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.IssuerCompanyReferalGetReq;
import com.maan.eway.admin.req.IssuerReferalGetReq;
import com.maan.eway.admin.res.IssuerReferalCompanyGetRes;
import com.maan.eway.admin.res.IssuerReferalCompniesRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginReferalService;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.error.Error;
import com.maan.eway.res.CommonRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "ADMIN : Login Referal ", description = "API's")
@RequestMapping("/admin")
public class LoginReferalController {
	
	@Autowired
	private  LoginReferalService entityService;
	
	@Autowired
	private LoginValidationService validationService ;

	@Autowired
	private PrintReqService reqPrinter;

//*************************************** Add Referaral Apis **********************************************************//	
		
	@PostMapping("/attachissuerreferal")
	@ApiOperation(value="This method is to Attach Issuer Referals")
	public ResponseEntity<CommonRes> attachIssuerReferals(@RequestBody  AttachIssuerReferalReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = validationService.validateIssuerReferalReq(req);
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.attachIssuerReferal(req);
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
	
//*************************************** Get Referal Apis **********************************************************//

	@PostMapping("/getissuerreferals")
	@ApiOperation(value="This method is to Get Issuer Referals")
	public ResponseEntity<CommonRes> getIssuerReferals(@RequestBody  IssuerReferalGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		List<IssuerReferalCompniesRes> res = entityService.getIssuerReferals(req);
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
	
//*************************************** Get One Company Referal Apis **********************************************************//

	@PostMapping("/getissueronebranchreferals")
	@ApiOperation(value="This method is to Get Issuer One Branch Referals")
	public ResponseEntity<CommonRes> getIssuerCompanyReferal(@RequestBody  IssuerCompanyReferalGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		List<IssuerReferalCompanyGetRes> res = entityService.getIssuerCompanyReferal(req);
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
