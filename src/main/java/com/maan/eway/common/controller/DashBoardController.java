/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-09-02 ( Date ISO 2022-09-02 - Time 18:14:51 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.DashBoardGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DasboardCountRes;
import com.maan.eway.common.res.DasboardListRes;
import com.maan.eway.common.res.DasboardRecentCusListRes;
import com.maan.eway.common.res.DasboardReferalPendingRes;
import com.maan.eway.common.service.DashBoardService;
import com.maan.eway.error.Error;


import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
* <h2>AdminTiraIntegrationController</h2>
*/
@RestController
@RequestMapping("/api")
@Api(tags = "3. DASHBOARD : Dash Board Grid ", description = "API's")
public class DashBoardController {
	@Autowired
	private DashBoardService entityService;

	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private DashBoardServiceV1 dashbordService;
	
	//Count
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("dashboard/count")
	public ResponseEntity<CommonRes> getallCount(@RequestBody  DashBoardGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<DasboardCountRes> res = entityService.getallCount(req);
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
	
	//List
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("dashboard/list")
	public ResponseEntity<CommonRes> getallList(@RequestBody  DashBoardGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		DasboardListRes res = entityService.getallList(req);
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
	//New Customer List
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("dashboard/recentcustomer")
		public ResponseEntity<CommonRes> getRecentCustomerList(@RequestBody  DashBoardGetReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			List<DasboardRecentCusListRes> res = entityService.getRecentCustomerList(req);
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
	//Referral pending List
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("dashboard/referalpendinglist")
		public ResponseEntity<CommonRes> getallReferalPendingbyLogin(@RequestBody  DashBoardGetReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			List<DasboardReferalPendingRes> res = entityService.getallReferalPendingbyLogin(req);
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
	//Renewal Quote
	/*	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("dashboard/list")
		public ResponseEntity<CommonRes> getallList(@RequestBody  DashBoardGetReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			List<DasboardListRes> res = entityService.getallList(req);
				data.setCommonResponse(res);
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				if (res != null) {
					return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				} else {
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}
			}*/
	@PostMapping("/dashboard/v1/count")
	public ResponseEntity<CommonRes> getallCountV1(@RequestBody  DashBoardGetReq req) {
		CommonRes data =dashbordService.getallCount(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/dashboard/v1/chart")
	public ResponseEntity<CommonRes> getChartDataV1(@RequestBody  DashBoardGetReq req) {
		CommonRes data =dashbordService.getChartModel(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/dashboard/v2/chart")
	public ResponseEntity<CommonRes> getChartDataV2(@RequestBody  DashBoardGetReq req) {
		CommonRes data =dashbordService.getChartModelV2(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/dashboard/v1/endorsement")
	public ResponseEntity<CommonRes> getEndorment(@RequestBody  DashBoardGetReq req) {
		CommonRes data =dashbordService.getgetEndormentV1(req);
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/dashboard/v3/chart")
	public ResponseEntity<CommonRes> getChartDataV3(@RequestBody  DashBoardGetReq req) {
		CommonRes data =dashbordService.getChartModelV3(req);

		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
}
