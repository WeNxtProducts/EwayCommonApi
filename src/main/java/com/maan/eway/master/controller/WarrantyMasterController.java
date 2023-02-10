package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.ExclusionMasterDropdownReq;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.req.WarrantyChangeStatusReq;
import com.maan.eway.master.req.WarrantyMasterDropdownReq;
import com.maan.eway.master.req.WarrantyMasterGetReq;
import com.maan.eway.master.req.WarrantyMasterGetallReq;
import com.maan.eway.master.req.WarrantyMasterListSaveReq;
import com.maan.eway.master.req.WarrantyMasterReq;
import com.maan.eway.master.req.WarrantyMasterSaveReq;
import com.maan.eway.master.res.ClausesMasterRes;
import com.maan.eway.master.res.WarrantyMasterRes;
import com.maan.eway.master.service.WarrantyMasterService;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Waranty MASTER", description="API's")
@RequestMapping("/master")
public class WarrantyMasterController {

@Autowired
private WarrantyMasterService service;

@Autowired
private PrintReqService reqPrinter;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/insertwarranty")
@ApiOperation(value="This Method is to save Waranty Master")
public ResponseEntity<CommonRes> saveWarranty(@RequestBody WarrantyMasterSaveReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	
List<Error> validation = service.validateWarranty(req);
//validation
if(validation !=null && validation.size()!=0) {
	data.setCommonResponse(null);
	data.setIsError(true);
	data.setErrorMessage(validation);
	data.setMessage("Failed");
	return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
} else {
	//save
	SuccessRes res = service.saveWarranty(req);
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

//  Get All Warranty Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/getallwarranty")
@ApiOperation("This method is getall Warranty")
public ResponseEntity<CommonRes> getallWarranty(@RequestBody WarrantyMasterGetallReq req)
{
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	
	List<WarrantyMasterRes> res =service.getallWarranty(req);
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

//  Get Active Warranty Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getactivewarranty")
	@ApiOperation("This method is get Active Warranty")
	public ResponseEntity<CommonRes> getActiveWarranty(@RequestBody WarrantyMasterGetallReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<WarrantyMasterRes> res = service.getActiveWarranty(req);
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

// Get By Warranty Id
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/getbywarrantyid")
@ApiOperation("This Method is to get by Warranty id")
public ResponseEntity<CommonRes> getByWarrantyId(@RequestBody WarrantyMasterGetReq req)
{
CommonRes data = new CommonRes();
WarrantyMasterRes res = service.getByWarrantyId(req);
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
	
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/warranty/changestatus")
@ApiOperation(value = "This method is get Warranty Change Status")
public ResponseEntity<CommonRes> changeStatusOfWarranty(@RequestBody WarrantyChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfWarranty(req);
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
//Warranty Master Drop Down Type
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value="/dropdown/warranty",produces = "application/json")
	@ApiOperation(value = "This method is get Warranty Master Drop Down")

	public ResponseEntity<DropdownCommonRes> getWarrantyMasterDropdown(@RequestBody WarrantyMasterDropdownReq req) {

		DropdownCommonRes data = new DropdownCommonRes();

		// Save
		List<DropDownRes> res = service.getWarrantyMasterDropdown(req);
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

	
	
	//List Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/insertwarrantylist")
	@ApiOperation(value="This Method is to save Waranty Master List")
	public ResponseEntity<CommonRes> saveWarranty(@RequestBody List<WarrantyMasterReq> req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
	List<Error> validation = service.validateWarrantyMultiInsert(req);
	//validation
	if(validation !=null && validation.size()!=0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
	} else {
		//save
		SuccessRes res = service.saveWarrantyMultiInsert(req);
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

@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallnonselectedwarranty")
	@ApiOperation("This method is getall Warranty Master")
	public ResponseEntity<CommonRes> getallNonSelectedWarranty(@RequestBody NonSelectedClausesGetAllReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<WarrantyMasterRes> res = service.getallNonSelectedWarranty(req);
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
