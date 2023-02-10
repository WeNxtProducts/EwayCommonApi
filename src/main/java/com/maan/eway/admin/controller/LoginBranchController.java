package com.maan.eway.admin.controller;

import java.util.Collection;
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

import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.BrokerBranchGetReq;
import com.maan.eway.admin.req.GetAllBrokerBranchReq;
import com.maan.eway.admin.req.GetBrokerBranchReq;
import com.maan.eway.admin.req.GetallBrokerBranchesReq;
import com.maan.eway.admin.req.IssuerBranchGetReq;
import com.maan.eway.admin.req.LoginBranchReq;
import com.maan.eway.admin.req.LoginBranchesSaveReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.res.BrokerCompanyGetRes;
import com.maan.eway.admin.res.GetBrokerBranchRes;
import com.maan.eway.admin.res.GetallBrokerBranchesRes;
import com.maan.eway.admin.res.IssuerCompanyGetRes;
import com.maan.eway.admin.res.LoginBranchRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginBranchService;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.error.Error;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "ADMIN : Login Branch ", description = "API's")
@RequestMapping("/admin")
public class LoginBranchController {

	@Autowired
	private  LoginBranchService entityService;
	
	@Autowired
	private LoginValidationService validationService ;

	@Autowired
	private PrintReqService reqPrinter;
	
//*************************************** Add Branches Apis **********************************************************//
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/attachbrokerbranches")
	@ApiOperation(value="This method is to Attach Broker Branches")
	public ResponseEntity<CommonRes> attachBrokerBranch(@RequestBody  AttachCompaniesReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = validationService.validateBrokerBranchReq(req);
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.attachBrokerBranches(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/attachissuerbranches")
	@ApiOperation(value="This method is to Attach Issuer Branches")
	public ResponseEntity<CommonRes> attachIssuerBranches(@RequestBody  AttachIssuerBrannchReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = validationService.validateIssuerBranchReq(req);
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.attachIssuerBranches(req);
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
	
//*************************************** Get Branches By Login ID **********************************************************//
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/getbrokerbranches")
	@ApiOperation(value="This method is to Get Broker Branches")
	public ResponseEntity<CommonRes> getBrokerBranches(@RequestBody  BrokerBranchGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		/////// get 
		List<BrokerCompanyGetRes> res = entityService.getBrokerBranches(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/getissuerbranches")
	@ApiOperation(value="This method is to Get Issuer Branches")
	public ResponseEntity<CommonRes> getIssuerBranches(@RequestBody  IssuerBranchGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		/////// get 
		List<IssuerCompanyGetRes> res = entityService.getIssuerBranches(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/attachbranches")
	@ApiOperation(value="This method is to Attach Broker Company Branches")
	public ResponseEntity<CommonRes> attachBrokerCompanyBranch(@RequestBody  AttachBrokerBranchReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = validationService.validateBrokerCompanyBranchReq(req);
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.attachBrokerCompanyBranch(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/getbrokercompanybranch")
	@ApiOperation(value="This method is to Attach Broker Company Branches")
	public ResponseEntity<CommonRes> getBrokerCompanyBranch(@RequestBody  GetBrokerBranchReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// save
		GetBrokerBranchRes res = entityService.getBrokerCompanyBranch(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/getallbrokercompanybranch")
	@ApiOperation(value="This method is to Attach Broker Company Branches")
	public ResponseEntity<CommonRes> getallBrokerCompanyBranch(@RequestBody  GetAllBrokerBranchReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// save
		List<GetBrokerBranchRes> res = entityService.getallBrokerCompanyBranch(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/getallbrokerbranches")
	@ApiOperation(value="This method is to Get all Broker Company Branches")
	public ResponseEntity<CommonRes> getallbrokerbranches(@RequestBody GetallBrokerBranchesReq req){
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
	List<GetallBrokerBranchesRes> res = entityService.getallBrokerBranches(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if(res!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/getallnonselecteduserbranches")
	@ApiOperation("This method is getall User Company Product Master")
	public ResponseEntity<CommonRes> getallNonSelectedUserCompanyBranches(@RequestBody UserCompanyProductGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<GetBrokerBranchRes> res = entityService.getallNonSelectedUserCompanyBranches(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/attachuserbranches")
	@ApiOperation("This method is to save branches as multi insert")
	public ResponseEntity<CommonRes> saveLoginBranches(@RequestBody LoginBranchesSaveReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<Error> validation = validationService.validateLoginBranches(req);
		if(validation!=null && validation.size() !=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			/////// save
			LoginCreationRes res = entityService.saveLoginBranches(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APRROVER,ROLE_ADMIN')")
	@PostMapping("/loginbranches")
	@ApiOperation("This Method is to get Login Branch and Broker Branches")
	public ResponseEntity<CommonRes> getloginbranches(@RequestBody LoginBranchReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<LoginBranchRes> res = entityService.getLoginbranches(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
	
	
	}
	
	
	

