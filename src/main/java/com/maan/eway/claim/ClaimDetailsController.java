package com.maan.eway.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.service.PrintReqService;

@RestController
@RequestMapping("/claim")
public class ClaimDetailsController {
	
	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private  ClaimDetailsService entityService ;
	
	@PostMapping(value = "/get/policydetails")
	public ResponseEntity<List<PolicyDetailsResponseDto>> policydetailsbyregno(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);
		
		List<PolicyDetailsResponseDto> data = entityService.policydetailsbyregno(req);
		if (data != null) {
			return new ResponseEntity<List<PolicyDetailsResponseDto>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/viewQuoteDetails")
	public ResponseEntity<ViewQuoteRes> claimViewQuoteDetails(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);
		
		ViewQuoteRes data = entityService.claimViewQuoteDetails(req);
		if (data != null) {
			return new ResponseEntity<ViewQuoteRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
