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
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CityMasterGetAllReq;
import com.maan.eway.master.req.CityMasterGetReq;
import com.maan.eway.master.req.ErrorDescMasterGetReq;
import com.maan.eway.master.req.ErrorDescMasterSaveReq;
import com.maan.eway.master.req.ProductMasterSaveReq;
import com.maan.eway.master.res.CityMasterRes;
import com.maan.eway.master.res.ErrorDescMasterRes;
import com.maan.eway.master.service.ErrorDescMasterService;
import com.maan.eway.res.SuccessRes;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/errormodules")
public class ErrorDescMasterController {
	
	@Autowired
	private ErrorDescMasterService errorDescMasterService;
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/inserterrormodules")
	@ApiOperation(value = "This method is Insert Error Modules Details")
	public ResponseEntity<com.maan.eway.common.res.CommonRes> insertProduct(@RequestBody ErrorDescMasterSaveReq req) {

		
		CommonRes data = new com.maan.eway.common.res.CommonRes();

		List<Error> validation = errorDescMasterService.validateErrorDesc(req);
		
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Get All
			SuccessRes res = errorDescMasterService.inserterrordesc(req);
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
	@PostMapping("/getallerrordetails")
	@ApiOperation("This method is getall error Details")
	public ResponseEntity<CommonRes> getallCityDetails(@RequestBody ErrorDescMasterGetReq req)
	{
		CommonRes data = new CommonRes();
		
		List<ErrorDescMasterRes> res = errorDescMasterService.getallErrorDetails(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getbyerrorcode")
	@ApiOperation("This Method is to get by error code")
	public ResponseEntity<CommonRes> getByCityId(@RequestBody ErrorDescMasterGetReq req)
	{
	CommonRes data = new CommonRes();
	ErrorDescMasterRes res = errorDescMasterService.getbyerrorcodeDetails(req);
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
