package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.Date;
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
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.service.EserviceCustomerDetailsService;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api")
@Api(tags = "3. COMMON : Eservice Customer Details ", description = "API's")
public class EserviceCustomerDetailsController {

	
	@Autowired 
	private EserviceCustomerDetailsService entityService ; 
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savecustomerdetails")
	public ResponseEntity<CommonRes> saveCustomerDetails(@RequestBody  EserviceCustomerSaveReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = entityService.validateCustomerDetails(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("1");
			comErrDescReq.setModuleName("CUSTOMER CREATION");
			
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
			SuccessRes res = entityService.saveCustomerDetails(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/customer")
	public ResponseEntity<CommonRes> saveCustomerLess(@RequestBody  EserviceCustomerSaveReq req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = entityService.validateCustomerDetails(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("1");
			comErrDescReq.setModuleName("CUSTOMER CREATION");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
		
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} 
		else {
			/////// save
			//req.setDobOrRegDate(new Date());
			req.setOccupation("12");
			req.setStateCode(null);
			req.setCityCode(null);
			req.setRegionCode(null);
			
			SuccessRes res = entityService.saveCustomerDetails(req);
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
	@PostMapping("/getcustomerdetails")
	public ResponseEntity<CommonRes> getMsPersonalInfo(@RequestBody GetCustomerDetailsReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	CustomerDetailsGetRes res = entityService.getCustomerDetails(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if(res!=null) {
		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
	}
	else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	}
			
	//Getall
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallcustomerdetails")
	public ResponseEntity<CommonRes> getallCustomerDetails(@RequestBody GetAllCustomerDetailsReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	List<CustomerDetailsGetRes> res = entityService.getallCustomerDetails(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getactivecustomerdetails")
	public ResponseEntity<CommonRes> getActiveCustomerDetails(@RequestBody GetAllCustomerDetailsReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	List<CustomerDetailsGetRes> res = entityService.getActiveCustomerDetails(req);
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

	// Search by Vr Tin No
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/searchcustomerdata")
	public ResponseEntity<CommonRes> getbyvrtinno(@RequestBody EserviceCustomerSearchVrtinReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<CustomerDetailsGetRes> res = entityService.getbyvrtinno(req);
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
