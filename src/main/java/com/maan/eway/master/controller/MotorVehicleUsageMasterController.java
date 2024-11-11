/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-09-23 ( Date ISO 2022-09-23 - Time 15:08:00 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.maan.eway.common.res.MotorVehicleUsageMasterGetRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ColorChangeStatusReq;
import com.maan.eway.master.req.MotorVehicleUsageChangeStatusReq;
import com.maan.eway.master.req.MotorVehicleUsageMasterGetReq;
import com.maan.eway.master.req.MotorVehicleUsageMasterGetallReq;
import com.maan.eway.master.req.MotorVehicleUsageMasterSaveReq;
import com.maan.eway.master.req.UsageDropDownReq;
import com.maan.eway.master.service.MotorVeicleUsageMasterService;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
* <h2>EserviceCustomerDetailsController</h2>
*/
@RestController
@RequestMapping("/api")
@Api(tags = "MOTOR VEHICLE USAGE MASTER", description = "API's")
public class MotorVehicleUsageMasterController {

	@Autowired
	private  MotorVeicleUsageMasterService entityService;
	
	@Autowired
	private PrintReqService reqPrinter;
	
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savemotorvehicleusagedetails")
	public ResponseEntity<CommonRes> saveMotorVehicleUsageDetails(@RequestBody  MotorVehicleUsageMasterSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = entityService.validateMotorVehicleUsageDetails(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			SuccessRes res = entityService.saveMotorVehicleUsageDetails(req);
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

	
	// Get
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getmotorvehicleusagedetails")
		public ResponseEntity<CommonRes> getMotorVehicleDetails(@RequestBody MotorVehicleUsageMasterGetReq req) {
			CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);
			MotorVehicleUsageMasterGetRes res = entityService.getMotorVehicleDetails(req);
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

		// Getall
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getallmotorvehicleusagedetails")
		public ResponseEntity<CommonRes> getallMotorVehicleDetails(@RequestBody MotorVehicleUsageMasterGetallReq req) {
			CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);
			List<MotorVehicleUsageMasterGetRes> res = entityService.getallMotorVehicleDetails(req);
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

		// Getactive
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
				@PostMapping("/getactivemotorvehicleusagedetails")
				public ResponseEntity<CommonRes> getactiveMotorVehicleDetails(@RequestBody MotorVehicleUsageMasterGetallReq req) {
					CommonRes data = new CommonRes();
					reqPrinter.reqPrint(req);
					List<MotorVehicleUsageMasterGetRes> res = entityService.getactiveMotorVehicleDetails(req);
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
				
				// Motor Vehicle Usage Master Drop Down Type
				@PostMapping(value="/dropdown/vehicleusage",produces = "application/json")
				@ApiOperation(value = "This method is get Motor Vehicle Usage Master Drop Down")

				public ResponseEntity<DropdownCommonRes> getVehicleUsageDropdown(@RequestBody UsageDropDownReq req ) {

					DropdownCommonRes data = new DropdownCommonRes();

					// Save
					List<DropDownRes> res = entityService.getVehicleUsageDropdown(req);
					data.setCommonResponse(res);
					data.setIsError(false);
					data.setErrorMessage(Collections.emptyList());
					data.setMessage("Success");

					if (res != null) {
						return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
					} else {
						return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
					}

				}
				

				// Motor Vehicle Usage Master Drop Down Type
				@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
				@PostMapping(value="/dropdown/induvidual/vehicleusage",produces = "application/json")
				@ApiOperation(value = "This method is get Motor Vehicle Usage Master Drop Down")

				public ResponseEntity<DropdownCommonRes> getInduvidualVehicleUsageDropdown(@RequestBody UsageDropDownReq req) {

					DropdownCommonRes data = new DropdownCommonRes();

					// Save
					List<DropDownRes> res = entityService.getInduvidualVehicleUsageDropdown(req);
					data.setCommonResponse(res);
					data.setIsError(false);
					data.setErrorMessage(Collections.emptyList());
					data.setMessage("Success");

					if (res != null) {
						return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
					} else {
						return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
					}

				}
				
				@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
				@PostMapping("/vehicleusage/changestatus")
				@ApiOperation(value = "This method is get Vehicle Usage Change Status")
				public ResponseEntity<CommonRes> changeStatusOfVehicleUsage(@RequestBody MotorVehicleUsageChangeStatusReq req) {

					CommonRes data = new CommonRes();
					// Change Status
					SuccessRes res = entityService.changeStatusOfVehicleUsage(req);
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
