package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;
import com.maan.eway.error.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.master.req.MotorMakeChangeStatusReq;
import com.maan.eway.master.req.MotorMakeGetAllReq;
import com.maan.eway.master.req.MotorMakeGetReq;
import com.maan.eway.master.req.MotorMakeSaveReq;
import com.maan.eway.master.res.MotorMakeGetRes;
import com.maan.eway.master.service.MotorMakeMasterService;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(tags = "MASTER : Motor Make Master ", description = "API's")
@RequestMapping("/master")
public class MotorMakeMasterController {

	@Autowired
	private MotorMakeMasterService service;
	@Autowired
	private PrintReqService reqPrinter;

	@PostMapping("/savemakemotor")
	@ApiOperation(value = "This method is Save Make Motor ")

	public ResponseEntity<CommonRes> saveMakeMotor(@RequestBody MotorMakeSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = service.validateMakeMotor(req);
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save

			SuccessRes res = service.saveMakeMotor(req);
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

	// Get By Make Id

	@PostMapping("/getmakeid")
	@ApiOperation(value = "This method is get by Make Id ")

	public ResponseEntity<CommonRes> getMakeId(@RequestBody MotorMakeGetReq req) {
		CommonRes data = new CommonRes();

		MotorMakeGetRes res = service.getMakeId(req);
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

	// Get All

	@PostMapping("/getallmotormake")
	@ApiOperation(value = "This method is Get all Motor Make ")

	public ResponseEntity<CommonRes> getallMotorMake(@RequestBody MotorMakeGetAllReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		// Get All
		List<MotorMakeGetRes> res = service.getallMotorMake(req);
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

	
	// Get All

		@PostMapping("/getactivemotormake")
		@ApiOperation(value = "This method is Get Active Motor Make ")

		public ResponseEntity<CommonRes> getactiveMotorMake(@RequestBody MotorMakeGetAllReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();

			// Get All
			List<MotorMakeGetRes> res = service.getactiveMotorMake(req);
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
		
		// Motor Make Drop Down Type
		@PostMapping("/dropdown/motormake")
		@ApiOperation(value = "This method is get Motor Make Master Drop Down")

		public ResponseEntity<CommonRes> getMotorMakeDropdown(@RequestBody MotorMakeGetAllReq req) {

			CommonRes data = new CommonRes();

			// Save
			List<DropDownRes> res = service.getMotorMakeDropdown(req);
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
		

		@PostMapping("/motormake/changestatus")
		@ApiOperation(value = "This method is Motor Make Change Status")
		public ResponseEntity<CommonRes> changeStatusOfMotorMake(@RequestBody MotorMakeChangeStatusReq req) {

			CommonRes data = new CommonRes();
			// Change Status
			SuccessRes res = service.changeStatusOfMotorMake(req);
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