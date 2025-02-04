/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-11-08 ( Date ISO 2022-11-08 - Time 16:28:23 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.FactorRateDetailsList;
import com.maan.eway.common.req.UpdateFactorRateReq;
import com.maan.eway.common.req.ViewPolicyCalc;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.UpdateCoverRes;
import com.maan.eway.error.Error;
import com.maan.eway.req.FactorFdCalcViewReq;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.service.PrintReqService;

/**
* <h2>FactorRateRequestDetailsController</h2>
*/
@RestController
@RequestMapping("/api")
public class FactorRateRequestDetailsController {

	@Autowired
	private  FactorRateRequestDetailsService entityService;
	
	@Autowired
	private PrintReqService reqPrinter;

/*
	private static final String ENTITY_TITLE = "FactorRateRequestDetails";


 	public FactorRateRequestDetailsController (FactorRateRequestDetailsService entityService) {
		this.entityService = entityService;
	}
*/
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savefactorratedetails")
	public ResponseEntity<CommonRes> saveFactorRateRequestDetails(@RequestBody  EserviceMotorDetailsSaveRes req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
	/*	List<Error> validation = entityService.validateMotorDetails(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else { */
			/////// save
			com.maan.eway.res.SuccessRes res = entityService.saveFactorRateRequestDetails(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		//}
    }
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/updatefactorrate")
	public ResponseEntity<CommonRes> updateFactorRatePremiumDetails(@RequestBody  UpdateFactorRateReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validateFoctorPremiumDetails(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else { 
			/////// save
			UpdateCoverRes res = entityService.updateFactorRatePremiumDetails(req);
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
	@PostMapping("/savefactors")
	public ResponseEntity<CommonRes> updateFactorIsSelectedDetails(@RequestBody  UpdateFactorRateReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validateFactorIsSelectedDetails(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else { 
			/////// save
			UpdateCoverRes res = entityService.updateFactorIsSelectedDetails(req);
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
	@PostMapping("/view/calc")
	public ResponseEntity<CommonRes> getFactorRateRequestDetails(@RequestBody  FactorRateDetailsGetReq req,@RequestHeader("Authorization") String tokens) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EservieMotorDetailsViewRes> res = entityService.getFactorRateRequestDetails(req,tokens.replaceAll("Bearer ", "").split(",")[0]);
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
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/view/policycalc")
	public ResponseEntity<CommonRes> getViewPolicyCalc(@RequestBody  FactorRateDetailsGetReq req,@RequestHeader("Authorization") String tokens) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		ViewPolicyCalc res = entityService.getViewPolicyCalc(req,tokens.replaceAll("Bearer ", "").split(",")[0]);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/updatepolicycalcrate")
	public ResponseEntity<CommonRes> updatePolicyCalcRate(@RequestBody  UpdateFactorRateReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validatePolicyCalcRate(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else { 
			/////// save
			UpdateCoverRes res = entityService.updatePolicyCalcRate(req);
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
    @GetMapping(value = "/factorraterequestdetails")
    public ResponseEntity<List<FactorRateRequestDetails>> getAllFactorRateRequestDetails() {
        List<FactorRateRequestDetails> lst = entityService.getAll();

        return new ResponseEntity<>(lst,HttpStatus.OK);
    }
/*
        @GetMapping(value = "/factorraterequestdetails/{id}")
    public ResponseEntity<FactorRateRequestDetails> getOneFactorRateRequestDetails(@PathVariable("id") long id) {

            FactorRateRequestDetails e = entityService.getOne(id);
            if (e == null) {
            	return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e, HttpStatus.OK);
    }

*/
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getfactorratedetailsList")
	public ResponseEntity<CommonRes> getFactorRateFdDetailsList(@RequestBody  FactorFdCalcViewReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		FactorRateDetailsList res = entityService.getFactorRateFdDetailsList(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//}
    }

}
