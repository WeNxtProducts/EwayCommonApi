package com.maan.eway.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.service.impl.referal.ReferalServiceImpl;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/calculator")
public class RatingCalculatorController {
	@Autowired
	private CalculatorEngine service;
	
	@Autowired
	private ReferalServiceImpl rservice;
	
	@PostMapping("/loadsection")
	@ApiOperation("This Method is to get by id")
	public void loadSection(@RequestBody CalcEngine request) {
		service.LoadSection(request);
	}
	
	/*@PostMapping("/loadfactorrates")visionmotor@123#
	@ApiOperation("This Method is to get by id")
	public void LoadFactorRates(@RequestBody CalcEngine request) {
		service.LoadFactorRates(request,"5");
	}*/
	
	@PostMapping("/calc")
	@ApiOperation("This Method is to get by id")
	public EserviceMotorDetailsSaveRes calc(@RequestBody CalcEngine request,@RequestHeader("Authorization") String tokens) {
		EserviceMotorDetailsSaveRes response = service.calculator(request,tokens.replaceAll("Bearer ", "").split(",")[0]); 
		return response;
	}
	
	@PostMapping("/referalcalc")
	@ApiOperation("This Method is to get by id")
	public EserviceMotorDetailsSaveRes referalcalc(@RequestBody CalcEngine request) {
		EserviceMotorDetailsSaveRes response = service.referalCalculator(request); 
		return response;
	}
	
	@PostMapping("/masterreferral")
	@ApiOperation("This Method is to get by id")
	public List<MasterReferal> masterreferral(@RequestBody CalcEngine request ,@RequestHeader("Authorization") String tokens ) {
		List<MasterReferal> response=null;
		try {
			System.out.println("T"+tokens);
			response = rservice.masterreferral(request,tokens.replaceAll("Bearer ", "").split(",")[0]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return response;
	}
	
}
