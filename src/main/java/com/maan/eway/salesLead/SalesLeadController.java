package com.maan.eway.salesLead;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;

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
}
