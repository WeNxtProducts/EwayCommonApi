package com.maan.eway.notif.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.notif.req.MailTriggerReq;
import com.maan.eway.notif.req.ReadMailReq;
import com.maan.eway.notif.req.ReplayMailTriggerReq;
import com.maan.eway.notif.req.SmsGetReq;
import com.maan.eway.notif.req.SmsReq;
import com.maan.eway.notif.res.ReadMailRes;
import com.maan.eway.notif.res.SmsGetRes;
import com.maan.eway.notif.service.NotificationService;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "NOTIFIACTION : Notifiaction ", description = "API's")
@RequestMapping("/post/notification")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private PrintReqService reqPrinter;

	@PostMapping("/readReceivedMail")
	@ApiOperation(value = "This method is to Read Email")
	public ResponseEntity<CommonRes> readMails(@RequestBody ReadMailReq req) {


		reqPrinter.reqPrint("Printer Request --->" + req);
		
		CommonRes data = new CommonRes();
		
		List<ReadMailRes> res = notificationService.ReadMail(req);

		if (res.size()!=0) {
			
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("No Recieved Mail for last 10 Days");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		
	}

	@PostMapping("/sendMail")
	@ApiOperation(value = "This method is to Send Email")
	public ResponseEntity<CommonRes> sendMails(@RequestBody MailTriggerReq req) {
		
		reqPrinter.reqPrint("Printer Request --->" + req);
		
		CommonRes data = new CommonRes();
		
		SuccessRes res = notificationService.SendMail(req);

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
	
	
	@PostMapping("/readSendMail")
	@ApiOperation(value = "This method is to Read Send Email")
	public ResponseEntity<CommonRes> readSentMail(@RequestBody ReadMailReq req) {


		reqPrinter.reqPrint("Printer Request --->" + req);
		
		CommonRes data = new CommonRes();
		
		List<ReadMailRes> res = notificationService.ReadSentMail(req);

		if (res.size()!=0) {
			
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("No Recieved Mail for last 10 Days");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		
	}
	
	
	@PostMapping("/sendSms")
	@ApiOperation(value = "This method is to Send Sms")
	public ResponseEntity<CommonRes> sendSms(@RequestBody SmsReq req) {
		
		reqPrinter.reqPrint("Printer Request --->" + req);
		
		CommonRes data = new CommonRes();
		
		SuccessRes res = notificationService.SaveAndSendSms(req);

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

	@PostMapping("/replayMail")
	@ApiOperation(value = "This method is to Send Replay Mail")
	public ResponseEntity<CommonRes> replayMail(@RequestBody ReplayMailTriggerReq req) {
		
		reqPrinter.reqPrint("Printer Request --->" + req);
		
		CommonRes data = new CommonRes();
		
		SuccessRes res = notificationService.replayMail(req);

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


	@PostMapping("/getSms")
	@ApiOperation(value = "This method is to Get Sms")
	public ResponseEntity<CommonRes> getSms(@RequestBody SmsGetReq req) {
		
		reqPrinter.reqPrint("Printer Request --->" + req);
		
		CommonRes data = new CommonRes();
		
		List<SmsGetRes> res = notificationService.getSms(req);

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
