package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.req.PaymentDetailsHistoryReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentInfoGetAllReq;
import com.maan.eway.common.req.PaymentInfoGetReq;
import com.maan.eway.common.req.TinyUrlGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.res.PaymentInfoGetRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/payment")
@Api(tags = "PAYMENT DETAILS", description = "API's")
public class PaymentController {

	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private  PaymentService service;
	
	// Payment Details Save
	@PostMapping("/makepayment")
	@ApiOperation(value="This method is to Save Make Payment")
	public ResponseEntity<CommonRes> makepayment(@RequestBody  MakePaymentSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation =  service.validatemakepayment(req);
		//Validation
		if(validation!=null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			MakePaymentRes res = service.savemakepayment(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if(res !=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
		}
	
	
	@PostMapping("/insertpaymentdetails")
	@ApiOperation(value="This method is to Save Make Payment")
	public ResponseEntity<CommonRes> savePaymentDetails(@RequestBody  PaymentDetailsSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation =  service.validatePaymentInsert(req);
		//Validation
		if(validation!=null && validation.size()!=0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else {
			PaymentDetailsSaveRes res = service.savePaymentDetails(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if(res !=null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	
		}

	@PostMapping("/gettinyurl")
	@ApiOperation(value = "This method is Get Quote Details")
	public ResponseEntity<CommonRes> getTinyUrl(@RequestBody TinyUrlGetReq req) {
		reqPrinter.reqPrint(req);
		
		// Save
		CommonRes res = service.getTinyUrl(req);
		
		if (res != null) {
			return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		

	}
	
	// Payment Details Get
//	@PostMapping("/getpaymentinfo")
//	@ApiOperation(value="This method is to Get Payment Details")
//	public ResponseEntity<CommonRes> getPaymentInfo(@RequestBody  PaymentInfoGetReq req) {
//		reqPrinter.reqPrint(req);
//		CommonRes data = new CommonRes();
//		PaymentInfoGetRes res = service.getPaymentInfo(req);
//			data.setCommonResponse(res);
//			data.setIsError(false);
//			data.setErrorMessage(Collections.emptyList());
//			data.setMessage("Success");
//			if(res !=null) {
//				return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//			}
//			else {
//				return new ResponseEntity<>(null, HttpStatus.CREATED);
//			}
//		}
//	
//				
//	// Payment Details Getall
//	@PostMapping("/viewpaymentinfo")
//	@ApiOperation(value="This method is to Get all Payment Details")
//	public ResponseEntity<CommonRes> viewPaymentInfo(@RequestBody  PaymentInfoGetAllReq req) {
//		reqPrinter.reqPrint(req);
//		CommonRes data = new CommonRes();
//		List<PaymentInfoGetRes> res = service.viewPaymentInfo(req);
//			data.setCommonResponse(res);
//			data.setIsError(false);
//			data.setErrorMessage(Collections.emptyList());
//			data.setMessage("Success");
//			if(res !=null) {
//				return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//			}
//			else {
//				return new ResponseEntity<>(null, HttpStatus.CREATED);
//			}
//		}
//		// Payment Details Update
//		@PostMapping("/updatepayment")
//		@ApiOperation(value="This method is to Update Make Payment")
//		public ResponseEntity<CommonRes> updatemakepayment(@RequestBody  MakePaymentUpdateReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//				SuccessRes res = service.updatemakepayment(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}
//		
//			
//		// Payment Details Get
//		@PostMapping("/getpaymentdetails")
//		@ApiOperation(value="This method is to Get Payment Details")
//		public ResponseEntity<CommonRes> getpaymentdetails(@RequestBody  PaymentDetailsGetReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//			PaymentDetailGetRes res = service.getpaymentdetails(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}
//		
//					
//		// Payment Details Getall
//		@PostMapping("/getallpaymentdetails")
//		@ApiOperation(value="This method is to Get all Payment Details")
//		public ResponseEntity<CommonRes> getallpaymentdetails(@RequestBody  PaymentDetailsGetallReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//			List<PaymentDetailGetRes> res = service.getallpaymentdetails(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}
//	
//
//
//		// Payment Details History
//		@PostMapping("/paymentdetailshistory")
//		@ApiOperation(value="This method is to Get Payment Details History")
//		public ResponseEntity<CommonRes> paymentdetailshistory(@RequestBody  PaymentDetailsHistoryReq req) {
//			reqPrinter.reqPrint(req);
//			CommonRes data = new CommonRes();
//			List<PaymentDetailGetRes> res = service.paymentdetailshistory(req);
//				data.setCommonResponse(res);
//				data.setIsError(false);
//				data.setErrorMessage(Collections.emptyList());
//				data.setMessage("Success");
//				if(res !=null) {
//					return new ResponseEntity<CommonRes>(data, HttpStatus.BAD_REQUEST);
//				}
//				else {
//					return new ResponseEntity<>(null, HttpStatus.CREATED);
//				}
//			}

		
	
		
}