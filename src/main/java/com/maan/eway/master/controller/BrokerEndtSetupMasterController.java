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

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerEndtSetupMasterChangeStatusReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterGetReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterGetallReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterSaveReq;
import com.maan.eway.master.req.EndorsementChangeStatusReq;
import com.maan.eway.master.req.EndorsementMasterDropdownReq;
import com.maan.eway.master.req.EndorsementMasterGetReq;
import com.maan.eway.master.req.EndorsementMasterGetallReq;
import com.maan.eway.master.req.EndorsementMasterSaveReq;
import com.maan.eway.master.res.BrokerEndtSetupMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterRes;
import com.maan.eway.master.service.BrokerEndtSetupMasterService;
import com.maan.eway.master.service.EndorsementMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : BROKER ENDT SETUP MASTER", description="API's")
@RequestMapping("/master")
public class BrokerEndtSetupMasterController {

@Autowired
private BrokerEndtSetupMasterService service;

@Autowired
private PrintReqService reqPrinter;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/insertbrokerendtsetup")
@ApiOperation(value = "This Method is to save Broker Endt Setup Master")
public ResponseEntity<CommonRes> saveBrokerEndtSetup(@RequestBody BrokerEndtSetupMasterSaveReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<Error> validation = service.validateBrokerEndtSetup(req);
//validation
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	} else {
		// save
		SuccessRes res = service.saveBrokerEndtSetup(req);
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

//  Get All Broker Endt Setup Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getallbrokerendtsetup")
@ApiOperation("This method is getall Broker Endt Setup")
public ResponseEntity<CommonRes> getallBrokerEndtSetup(@RequestBody BrokerEndtSetupMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<BrokerEndtSetupMasterGetallRes> res = service.getallBrokerEndtSetup(req);
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

//  Get Active Broker Endt Setup
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getactivebrokerendtsetup")
@ApiOperation("This method is get Active Broker Endt Setup")
public ResponseEntity<CommonRes> getActiveBrokerEndtSetup(@RequestBody BrokerEndtSetupMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<BrokerEndtSetupMasterGetallRes> res = service.getActiveBrokerEndtSetup(req);
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

// Get By Id
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getbybrokerendtsetupid")
@ApiOperation("This Method is to get by Broker Endt Setup id")
public ResponseEntity<CommonRes> getBybrokerEndtSetupId(@RequestBody BrokerEndtSetupMasterGetReq req) {
	CommonRes data = new CommonRes();
	BrokerEndtSetupMasterGetallRes res = service.getBybrokerEndtSetupid(req);
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
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/brokerendtsetup/changestatus")
@ApiOperation(value = "This method is get Broker Endt Setup Change Status")
public ResponseEntity<CommonRes> changeStatusOfBrokerEndtSetup(@RequestBody BrokerEndtSetupMasterChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfBrokerEndtSetup(req);
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
