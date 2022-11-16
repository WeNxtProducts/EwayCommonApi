package com.maan.eway.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.service.FactorRateRequestDetailsService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/calculator")
public class RatingCalculatorController {
	@Autowired
	private CalculatorEngine service;
	@Autowired
	private FactorRateRequestDetailsService fservice;
	@PostMapping("/loadsection")
	@ApiOperation("This Method is to get by id")
	public void loadSection(@RequestBody CalcEngine request) {
		service.LoadSection(request);
	}
	
	/*@PostMapping("/loadfactorrates")
	@ApiOperation("This Method is to get by id")
	public void LoadFactorRates(@RequestBody CalcEngine request) {
		service.LoadFactorRates(request,"5");
	}*/
	
	@PostMapping("/calc")
	@ApiOperation("This Method is to get by id")
	public EserviceMotorDetailsSaveRes calc(@RequestBody CalcEngine request) {
		List<Cover> calculator = service.calculator(request);
		
		EserviceMotorDetailsSaveRes response=new EserviceMotorDetailsSaveRes();
		response.setCoverList(calculator);
		response.setResponse("Saved Successfully");
		response.setRequestReferenceNo(request.getRequestReferenceNo());
		//response.setCustomerReferenceNo(req.getCustomerReferenceNo());
		response.setVehicleId(request.getVehicleId()) ;	
		response.setVdRefNo(request.getVdRefNo());
		response.setCdRefNo(request.getCdRefNo());
		response.setInsuranceId(request.getInsuranceId());
		response.setSectionId(request.getSectionId());
		response.setCreatedBy(request.getCreatedBy());
		response.setProductId(request.getProductId()); 
		response.setMsrefno(request.getMsrefno());
		
		fservice.saveFactorRateRequestDetails(response);
		
		return response;
	}
	
	
	
}
