package com.maan.eway.jasper.controller;

import java.util.Collections;


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
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PdfJsonReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/pdf")
@Api(tags = "REPORT : Jasper Reports", description = "API's")
public class JasperController {
	
	@Autowired
	private JasperService jasper;
	@Autowired
	private  PrintReqService printReq;
	
	@PostMapping("/policyform") 
	private ResponseEntity<CommonRes> policyform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.policyform(req);;
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
	
	@PostMapping("/proposalform") 
	private ResponseEntity<CommonRes> proposalform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.proposalform(req);;
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
	
	
	@PostMapping("/policyreport") 
	private ResponseEntity<CommonRes> policyreportform(@RequestBody JasperReportDocReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		
		JasperDocumentRes res = jasper.policyreportform(req);
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
	
	@GetMapping("/taxInvoice")
	private ResponseEntity<CommonRes> taxInvoice(@RequestParam ("quoteNo") String quoteNo){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.taxInvoice(quoteNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/creditNote")
	private ResponseEntity<CommonRes>  creditNote(@RequestParam ("quoteNo") String quoteNo){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.creditNote(quoteNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/premium/report")
	public CommonRes getPremiumReport(@RequestBody PremiumReportReq req) {
		return jasper.getPremiumReport(req);
	}
	
	@PostMapping("/getPremiumReportDetails")
	public CommonRes getPremiumReportDetails(@RequestBody PremiumReportReq req) {
		return jasper.getPremiumReportDetails(req);
	}
	@PostMapping("/illustration/{JsonFile}")
	public ResponseEntity<JasperDocumentRes> illustration(@PathVariable("JsonFile") String jsonFile) {
		//CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.illustration(jsonFile);
		/*data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");*/
		if(res != null) {
			return new ResponseEntity<JasperDocumentRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/InalipaSchedule")
	public ResponseEntity<CommonRes> getInalipaSchedule(@RequestParam ("policyNo") String policyNo) {
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.getInalipaSchedule(policyNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getSchedule")
	public CommonRes getSchedule(@RequestBody JasperScheduleReq req) {
		return jasper.getSchedule(req);
	}
	
	@PostMapping("/json/Response")
	public CommonRes PdfJsonResponse(@RequestBody PdfJsonReq req) {
		return jasper.PdfJsonResponse(req);
	}
	
	@GetMapping("/getReportByRequestRefNo")
	public ResponseEntity<?> GetReportByRequestRefNo(@RequestParam(value = "requestRefNo",required = true) String requestRefNo){
		CommonRes data = new CommonRes();
		JasperDocumentRes res = jasper.GetReportByRequestRefNo(requestRefNo);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		if(res !=null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
}
