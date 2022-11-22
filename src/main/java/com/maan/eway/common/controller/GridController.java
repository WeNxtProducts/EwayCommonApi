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

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.service.GridService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api")
@Api(tags = "GRID DETAILS", description = "API's")
public class GridController {

	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private  GridService entityService;
	
	@PostMapping("/existingquotedetails")
	public ResponseEntity<CommonRes> getallExistingQuoteDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallExistingQuoteDetails(req);
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
