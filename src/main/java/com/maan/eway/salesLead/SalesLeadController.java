package com.maan.eway.salesLead;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/sales")
public class SalesLeadController {
	
	@Autowired
	private SalesLeadService service;
	
	@PostMapping("/insertSales")
	public ResponseEntity<?> insertSales(@RequestBody InsertSalesReq req){
		CommonRes res = service.insertSales(req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping("/getSalesLead")
	public ResponseEntity<?> getSalesLead(@RequestParam (value = "leadId",required = false) String leadId){
		CommonRes res = service.getSalesLead(leadId);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@PostMapping("/insertEnquiry")
	public ResponseEntity<?> insertEnquiry(@RequestBody EnquiryDetailsDTO req){
		CommonRes res = service.insertEnquiry(req);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping("/getEnquirys")
	public ResponseEntity<?> getAllEnquiry(@RequestParam (value = "enquiryId",required = false) String enquiryId){
		CommonRes res = service.getEnquirys(enquiryId);
		if(res!=null) {
			return new ResponseEntity<CommonRes>(res,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
		}
	}
	
	/// Drop  down api //
	@PostMapping("/contactType")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> contactType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.contactType(req);
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
	
	@PostMapping("/sectionType")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> sectionType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.sectionType(req);
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
	
	@PostMapping("/channel")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> customerType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.customerType(req);
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
	
	@PostMapping("/typeOfBusiness")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> typeOfBusiness(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.typeOfBusiness(req);
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
	
	@PostMapping("/currentInsurer")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> currentInsurer(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.currentInsurer(req);
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
	
	@PostMapping("/lineOfBusiness")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> lineOfBusiness(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.lineOfBusiness(req);
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
	
	@PostMapping("/product")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> product(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.product(req);
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
	
	@PostMapping("/businessType")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> businessType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.businessType(req);
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
	
	@PostMapping("/pos")
	@ApiOperation(value = "This method is to get contact type Drop Down")
	public ResponseEntity<CommonRes> probabilityOfSuccess(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.probabilityOfSuccess(req);
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
	
	@PostMapping("insert/personalInfo/{enquiryId}")
	public ResponseEntity<?> insertPersonalInfo(@PathVariable ("enquiryId") String enquiryId){
		//CommonRes res = service.insertPersonalInfo(enquiryId);
		return null;
	}
	
}
