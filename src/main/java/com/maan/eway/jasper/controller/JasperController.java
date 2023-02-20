package com.maan.eway.jasper.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.service.PrintReqService;

@RestController
@RequestMapping("/pdf")
public class JasperController {
	
	@Autowired
	private JasperService jasper;
	@Autowired
	private  PrintReqService printReq;
	
	@PostMapping("/policyform") 
	private ResponseEntity<CommonRes> policyform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.policyform(req);;
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
	
	@PostMapping("/proposalform") 
	private ResponseEntity<CommonRes> proposalform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.proposalform(req);;
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
