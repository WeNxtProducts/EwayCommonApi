package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ClausesChangeStatusReq;
import com.maan.eway.master.req.ClausesMasterDropdownReq;
import com.maan.eway.master.req.ClausesMasterGetReq;
import com.maan.eway.master.req.ClausesMasterGetallReq;
import com.maan.eway.master.req.ClausesMasterSaveReq;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.res.ClausesMasterRes;
import com.maan.eway.master.service.ClausesMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Waranty MASTER", description="API's")
@RequestMapping("/master")
public class ClausesMasterController {

@Autowired
private ClausesMasterService service;

@Autowired
private PrintReqService reqPrinter;

//Save

@PostMapping("/insertclauses")
@ApiOperation(value = "This Method is to save Waranty Master")
public ResponseEntity<CommonRes> saveClauses(@RequestBody ClausesMasterSaveReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<Error> validation = service.validateClauses(req);
//validation
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	} else {
		// save
		SuccessRes res = service.saveClauses(req);
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

//  Get All Clauses Master

@PostMapping("/getallclauses")
@ApiOperation("This method is getall Clauses")
public ResponseEntity<CommonRes> getallClauses(@RequestBody ClausesMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<ClausesMasterRes> res = service.getallClauses(req);
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

//  Get Active Clauses Master

@PostMapping("/getactiveclauses")
@ApiOperation("This method is get Active Clauses")
public ResponseEntity<CommonRes> getActiveClauses(@RequestBody ClausesMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<ClausesMasterRes> res = service.getActiveClauses(req);
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

// Get By Clauses Id

@PostMapping("/getbyclausesid")
@ApiOperation("This Method is to get by Clauses id")
public ResponseEntity<CommonRes> getByClausesId(@RequestBody ClausesMasterGetReq req) {
	CommonRes data = new CommonRes();
	ClausesMasterRes res = service.getByClausesId(req);
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

@PostMapping("/clauses/changestatus")
@ApiOperation(value = "This method is get Clauses Change Status")
public ResponseEntity<CommonRes> changeStatusOfClauses(@RequestBody ClausesChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfClauses(req);
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

//Clauses Master Drop Down Type
@PostMapping(value="/dropdown/clauses",produces = "application/json")
@ApiOperation(value = "This method is get Clauses Master Drop Down")

public ResponseEntity<DropdownCommonRes> getClausesMasterDropdown(@RequestBody ClausesMasterDropdownReq req) {

	DropdownCommonRes data = new DropdownCommonRes();

	// Save
	List<DropDownRes> res = service.getClausesMasterDropdown(req);
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

@PostMapping("/getallnonselectedwars")
@ApiOperation("This method is getall Company Product Master")
public ResponseEntity<CommonRes> getallNonSelectedCompanyProducts(@RequestBody NonSelectedClausesGetAllReq req)
{
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	
	List<ClausesMasterRes> res = service.getallNonSelectedWars(req);
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

}
