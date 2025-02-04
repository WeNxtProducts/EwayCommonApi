/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:27 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
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

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.res.IndustryDropDownCommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CountryGetAllReq;
import com.maan.eway.master.req.CountryMasterGetReq;
import com.maan.eway.master.req.CountryMasterSaveReq;
import com.maan.eway.master.req.IndustryMasterChangeStatusReq;
import com.maan.eway.master.req.IndustryMasterDropdownReq;
import com.maan.eway.master.req.IndustryMasterGetReq;
import com.maan.eway.master.req.IndustryMasterGetallReq;
import com.maan.eway.master.req.IndustryMasterSaveReq;
import com.maan.eway.master.res.CountryMasterRes;
import com.maan.eway.master.res.IndustryMasterRes;
import com.maan.eway.master.service.IndustryMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.IndustryDropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
* <h2>InsuranceCompanyMasterController</h2>
*/
@RestController
@Api(  tags="2. COMPANY CONFIG : Industry Master ", description = "API's")
@RequestMapping("/master")
public class IndustryMasterController {

	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private IndustryMasterService service;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
		
	
	// Industry Master Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping(value="/dropdown/industry",produces = "application/json")
		@ApiOperation(value = "This method is get Industry Master Drop Down")

		public ResponseEntity<IndustryDropDownCommonRes> getIndustryMasterDropdown( @RequestBody IndustryMasterDropdownReq req ) {

		IndustryDropDownCommonRes data = new IndustryDropDownCommonRes();

			// Save
			List<IndustryDropDownRes> res = service.getIndustryMasterDropdown(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<IndustryDropDownCommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}

	
	// save
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping("/saveindustry")
		@ApiOperation(value = "This method is Insert Industry Master")
		public ResponseEntity<CommonRes> insertIndustry(@RequestBody IndustryMasterSaveReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes =  service.validateIndustryDetails(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}

		
			// validation
			if (validation != null && validation.size() != 0) {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(validation);
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

			} else {

				// Get All
				SuccessRes res = service.insertIndustry(req);
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
		
		//  Get All Industry Master
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping("/getallindustry")
		@ApiOperation("This method is getall Country Details")
		public ResponseEntity<CommonRes> getallIndustry(@RequestBody IndustryMasterGetallReq req )
		{
			CommonRes data = new CommonRes();	
			List<IndustryMasterRes> res = service.getallIndustry(req);
			data.setCommonResponse(res);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");
			
			if(res!= null) {
				return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
			}
		}
		
	//  Get Active Industry Master
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping("/getactiveindustry")
			@ApiOperation("This method is get Active Industry Details")
			public ResponseEntity<CommonRes> getActiveIndustryMaster(@RequestBody IndustryMasterGetallReq req )
			{
				CommonRes data = new CommonRes();
				
				List<IndustryMasterRes> res = service.getActiveIndustryMaster(req);
				data.setCommonResponse(res);
				data.setErrorMessage(Collections.emptyList());
				data.setIsError(false);
				data.setMessage("Success");
				
				if(res!= null) {
					return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
				}
				else {
					return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
				}
			}
		
		// Get By Country Id
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping("/getbyindustryid")
		@ApiOperation("This Method is to get by Country id")
		public ResponseEntity<CommonRes> getByIndustryId(@RequestBody IndustryMasterGetReq req)
		{
		CommonRes data = new CommonRes();
		IndustryMasterRes res = service.getByIndustryId(req);
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

	
	// Get By Country Id
		@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
		@PostMapping("/industrychangestatus")
		@ApiOperation("This Method is to Change Status")
		public ResponseEntity<CommonRes> changeStatus(@RequestBody IndustryMasterChangeStatusReq req)
		{
		CommonRes data = new CommonRes();
		SuccessRes res = service.changeStatus(req);
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
