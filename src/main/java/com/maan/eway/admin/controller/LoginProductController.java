package com.maan.eway.admin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.BrokerCompanyProductGetReq;
import com.maan.eway.admin.req.BrokerCompanyProductsGetRes;
import com.maan.eway.admin.req.BrokerProductGetReq;
import com.maan.eway.admin.res.BrokerProductGetRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginProductService;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerCompanyProductReq;
import com.maan.eway.master.req.BrokerProductChangeReq;
import com.maan.eway.master.req.BrokerProductReq;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "ADMIN : Login Product ", description = "API's")
@RequestMapping("/admin")
public class LoginProductController {


	@Autowired
	private  LoginProductService entityService;
	
	@Autowired
	private LoginValidationService validationService ;

	@Autowired
	private PrintReqService reqPrinter;
	
	
//*************************************** Add Products Apis **********************************************************//
		
	@PostMapping("/attachbrokerproducts")
	@ApiOperation(value="This method is to Attach Broker Products")
	public ResponseEntity<CommonRes> attachBrokerProducts(@RequestBody  AttachCompnayProductRequest req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = validationService.validateBrokerProductReq(req);
		//// validation
		if (validation != null && validation.size() != 0) 	{
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save
			LoginCreationRes res = entityService.saveBrokerProductDetails(req);
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

//*************************************** Get Products Apis **********************************************************//
	
	@PostMapping("/getbrokerproductbyid")
	@ApiOperation(value="This method is to Get Broker Products")
	public ResponseEntity<CommonRes> getBrokerProducts(@RequestBody  BrokerProductGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		BrokerProductGetRes res = entityService.getBrokerProducts(req);
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
	
	
//*************************************** Get One CompanyProducts Apis **********************************************************//	
	

	@PostMapping("/getbrokercompanyproducts")
	@ApiOperation(value="This method is to Get Broker Company Products")
	public ResponseEntity<CommonRes> getBrokerCompanyProducts(@RequestBody  BrokerCompanyProductGetReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// get
		List<BrokerCompanyProductsGetRes> res = entityService.getBrokerCompanyProducts(req);
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
	
	
//  Get All Cover Master
	@PostMapping("/updatebrokercompanyproducts")
	@ApiOperation(value = "This method is Insert Company Product Master")
	public ResponseEntity<CommonRes> insertCompanyProducts(@RequestBody BrokerCompanyProductReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<Error> validation = entityService.validateUpdateBrokerCompanyProductDetails(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {

			// Save
			SuccessRes res = entityService.updateBrokerCompanyProductDetails(req);
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
	
	
	@PostMapping("/getallnonselectedbrokerproducts")
	@ApiOperation("This method is getall Company Product Master")
	public ResponseEntity<CommonRes> getallNonSelectedBrokerCompanyProducts(@RequestBody BrokerCompanyProductGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<CompanyProductMasterRes> res = entityService.getallNonSelectedBrokerCompanyProducts(req);
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
	
	@PostMapping("/brokercompanyproducts/changestatus")
	@ApiOperation(value = "This method is get Company Product Master Drop Down")

	public ResponseEntity<CommonRes> changeStatusOfCompanyProduct(@RequestBody BrokerProductChangeReq req) {

		CommonRes data = new CommonRes();
		// Change Status
		SuccessRes res = entityService.changeStatusOfCompanyProduct(req);
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
	
	// Branch Master Drop Down Type
	@PostMapping("/dropdown/brokerproducts")
	@ApiOperation(value = "This method is get Branch Master Drop Down")

	public ResponseEntity<CommonRes> getBrokerProductDropdown(@RequestBody BrokerProductReq req ) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = entityService.getBrokerProductDropdown(req);
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
