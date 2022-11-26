package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.master.req.ExchangeChangeStatusReq;
import com.maan.eway.master.req.ExchangeMasterGetReq;
import com.maan.eway.master.req.ExchangeMasterGetallReq;
import com.maan.eway.master.req.ExchangeMasterSaveReq;
import com.maan.eway.master.req.OccupationChangeStatusReq;
import com.maan.eway.master.res.ExchangeMasterGetRes;
import com.maan.eway.master.service.ExchangeMasterService;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;
import com.maan.eway.error.Error;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Exchange Master", description = "API's")
@RequestMapping("/master")
public class ExchangeMasterController {

	@Autowired
	private ExchangeMasterService service;

	@Autowired
	private PrintReqService reqPrinter;

	// save
	@PostMapping("/insertexchangemaster")
	@ApiOperation(value = "This method is to Insert Exchange Master")
	public ResponseEntity<CommonRes> insertExchangeMaster(@RequestBody ExchangeMasterSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = service.validateInsertExchangeMaster(req);
		// Validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Save
			SuccessRes res = service.insertExchangeMaster(req);
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
	@PostMapping("/getexchangemaster")
	public ResponseEntity<CommonRes> getExchangeMaster(@RequestBody ExchangeMasterGetReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		ExchangeMasterGetRes res = service.getExchangeMaster(req);
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
	@PostMapping("/getallexchangemaster")
	public ResponseEntity<CommonRes> getallExchangeMaster(@RequestBody ExchangeMasterGetallReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<ExchangeMasterGetRes> res = service.getallExchangeMaster(req);
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

	// Active

	@PostMapping("/getactiveexchange")
	public ResponseEntity<CommonRes> getActiveExchange(@RequestBody ExchangeMasterGetallReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<ExchangeMasterGetRes> res = service.getActiveExchange(req);
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

	// Exchange Master Drop Down Type
		@GetMapping("/dropdown/exchange")
		@ApiOperation(value = "This method is get Exchange Master Drop Down")

		public ResponseEntity<CommonRes> getExchangeMasterDropdown() {

			CommonRes data = new CommonRes();

			// Save
			List<DropDownRes> res = service.getExchangeMasterDropdown();
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
	@PostMapping("/exchange/changestatus")
	@ApiOperation(value = "This method is get Exchange Change Status")
	public ResponseEntity<CommonRes> changeStatusOfExchange(@RequestBody ExchangeChangeStatusReq req) {

		CommonRes data = new CommonRes();
		// Change Status
		SuccessRes res = service.changeStatusOfExchange(req);
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