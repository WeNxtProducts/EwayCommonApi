package com.maan.eway.renewal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.renewal.req.GetCustomersByBrokerReq;
import com.maan.eway.renewal.req.RenewalTrackingInReq;
import com.maan.eway.renewal.req.RtGetProductsReq;
import com.maan.eway.renewal.res.RenewalTrackByProductRes;
import com.maan.eway.renewal.res.RenewalTrackingDetails;
import com.maan.eway.renewal.res.RenewalTrackingInRes;
import com.maan.eway.renewal.service.RenewalTrackingService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/renewaltrack")
@Api(tags = "Track the renewal policy")
public class RenewalTrackingController {

	@Autowired
	private RenewalTrackingService service;

	@PostMapping("/byapprover")
	public RenewalTrackingInRes renewalTrackByApprover(@RequestBody RenewalTrackingInReq req) {
		RenewalTrackingInRes res = service.renewTrackByApprover(req);
		return res;
	}

	@PostMapping("/getcustomersbybroker")
	public ResponseEntity<List<RenewalTrackingDetails>> getCustomerDetailsByBroker(
			@RequestBody GetCustomersByBrokerReq req) {
		List<RenewalTrackingDetails> res = service.getBrokersCustomerList(req);
		if (res != null) {
			return new ResponseEntity<List<RenewalTrackingDetails>>(res, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	
	@PostMapping("/getproductperformance")
	public RenewalTrackByProductRes renewTrackForProductPerf(@RequestBody RenewalTrackingInReq req) {
		RenewalTrackByProductRes  res=service.renewTrackForProductPerf(req);
		return res;
	}
	
	
	@PostMapping("/getAllproducts")
	public ResponseEntity<List<RenewalTrackingDetails>> getAllByProductCode(
			@RequestBody RtGetProductsReq req) {
		List<RenewalTrackingDetails> res = service.getAllByProductCode(req);
		if (res != null) {
			return new ResponseEntity<List<RenewalTrackingDetails>>(res, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}

}
