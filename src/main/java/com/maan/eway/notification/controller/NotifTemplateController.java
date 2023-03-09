package com.maan.eway.notification.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.service.NotifTemplateService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "TEMPLATES : TEMPLATES ", description = "API's")
@RequestMapping("/notification")
public class NotifTemplateController {

	@Autowired
	private  NotifTemplateService notifTempService;
	
	@Autowired
	private  PrintReqService reqPrinter;
	
	// Client Type
		@PostMapping("/dropdown/templateslist")
		@ApiOperation(value = "This method is to Mail Templates Drop Down")

		public ResponseEntity<CommonRes> getTemplatesDropDown(@RequestBody  TemplatesDropDownReq req ) {

			CommonRes data = new CommonRes();

			// Save
			List<DropDownRes> res = notifTempService.getTemplatesDropDown(req);
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
		
		
		
		
		@PostMapping("/getmailtemplate")
		@ApiOperation(value = "This method is to Get Framed Mail Template Details")
		public ResponseEntity<CommonRes> getMailTemplate(@RequestBody NotifTemplateGetReq req) {

			reqPrinter.reqPrint("Printer Request --->" + req);
			CommonRes data = new CommonRes();

			// Save
			CommonRes res = notifTempService.getMailTemplate(req);
			if (res.getIsError() == false ) {
				data.setCommonResponse(res.getCommonResponse() );
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(res.getErrorMessage() );
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
		}
		
		@PostMapping("/getsmstemplate")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> getSmsTemplate(@RequestBody NotifTemplateGetReq req) {

			reqPrinter.reqPrint("Printer Request --->" + req);
			CommonRes data = new CommonRes();

			// Save
			CommonRes res = notifTempService.getSmsTemplate(req);
			if (res.getIsError() == false ) {
				data.setCommonResponse(res.getCommonResponse() );
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(res.getErrorMessage() );
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
		}
		
	
}
