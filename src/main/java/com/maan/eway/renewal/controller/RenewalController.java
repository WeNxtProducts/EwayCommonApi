package com.maan.eway.renewal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.pullrenewalReq;
import com.maan.eway.renewal.service.RenewalService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "RENEWAL : renewal ", description = "API's")
@RequestMapping("/post/renewal")
public class RenewalController {
	
	@Autowired
	private RenewalService renewalservice;
	
	@PostMapping("/pullrenewal")
	public ResponseEntity<CommonRes> pullrenewal(@RequestBody pullrenewalReq request) {
	 	CommonRes data = renewalservice.pullrenewal(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
}
