package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.GetOptedSectionAdditionalInfoReq;
import com.maan.eway.master.res.GetOptedSectionAdditionalInfoRes;
import com.maan.eway.master.service.ProductSectionAdditionalInfoMasterService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "MASTER : Product Section Additional Info Master", description = "API's")
@RequestMapping("/master")
public class ProductSectionAdditionalInfoMasterController {
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private ProductSectionAdditionalInfoMasterService service;
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getoptedsectionadditionalinfo") 	//based on product and opted sections
	public ResponseEntity<CommonRes> getOptedSectionAdditionalInfo(@RequestBody GetOptedSectionAdditionalInfoReq req){
		CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);
			List<GetOptedSectionAdditionalInfoRes> res = service.getOptedSectionAdditionalInfo(req);
			data.setCommonResponse(res);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");
			if(res!=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	

}
