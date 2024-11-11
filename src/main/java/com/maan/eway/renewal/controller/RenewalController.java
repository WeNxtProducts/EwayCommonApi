package com.maan.eway.renewal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.req.RenewalCopyQuoteReq;
import com.maan.eway.renewal.req.RenewalPendingRequest;
import com.maan.eway.renewal.service.RenewalService;
import com.maan.eway.res.CopyQuoteSuccessRes;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "RENEWAL : renewal ", description = "API's")
@RequestMapping("/post/renewal")
public class RenewalController {
	
	@Autowired
	private RenewalService renewalservice;
	
	@PostMapping("/pullrenewal")
	public ResponseEntity<CommonRes> pullrenewal(@RequestBody PullrenewalReq request) {
	 	CommonRes data = renewalservice.pullrenewal(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/renewalCopyQuote")
	public ResponseEntity<CopyQuoteSuccessRes> renewalCopyQuote(@RequestBody RenewalCopyQuoteReq request) {
		CopyQuoteSuccessRes data = renewalservice.renewalCopyQuote(request);
	 	if (data != null) {
			return new ResponseEntity<CopyQuoteSuccessRes>(data, HttpStatus.CREATED);
	 	} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/pending")
	public ResponseEntity<CommonRes> getRenewalPending(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalPending(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/expired")
	public ResponseEntity<CommonRes> getRenewalExpired(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalExpired(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/completed")
	public ResponseEntity<CommonRes> getRenewalCompleted(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalCompleted(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction")
	public ResponseEntity<CommonRes> getRenewalTransaction(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalTransaction(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/success")
	public ResponseEntity<CommonRes> getRenewalTransactionSuccess(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalTransactionSuccess(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/converted")
	public ResponseEntity<CommonRes> getRenewalTransactionCoverted(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalTransactionConverted(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/pending")
	public ResponseEntity<CommonRes> getRenewalTransactionPending(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalTransactionPending(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
}
