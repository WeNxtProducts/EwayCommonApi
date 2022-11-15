package com.maan.eway.notif.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.error.Error;
import com.maan.eway.notif.req.SmsGetReq;
import com.maan.eway.notif.req.SmsInsertReq;
import com.maan.eway.notif.res.SmsMasterGetRes;
import com.maan.eway.notif.service.SmsMasterService;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/master")
@Api(tags = "MASTER : SMS Master", description = "API's")
public class SmsMasterController {

	@Autowired
	private SmsMasterService smsService;

	@Autowired
	private PrintReqService reqPrinter;

	// Save
	@PostMapping("/insertsmsmaster")
	@ApiOperation(value = "This method is to Insert Sms Master")
	private ResponseEntity<CommonRes> insertsmsmaster(@RequestBody SmsInsertReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = smsService.validatesmsmaster(req);
		// Validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			// Get all
			SuccessRes res = smsService.insertsmsmaster(req);
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

// Get By Id

	@PostMapping("/getbysmsid")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<CommonRes> getbysmsid(@RequestBody SmsGetReq req) {
		CommonRes data = new CommonRes();
		SmsMasterGetRes res = smsService.getbysmsid(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
