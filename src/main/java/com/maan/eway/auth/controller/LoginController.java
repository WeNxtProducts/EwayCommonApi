package com.maan.eway.auth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.auth.service.LoginValidatedService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(  tags="LOGIN : Login Token Creation", description = "API.")
@RequestMapping("/authentication")
public class LoginController {
	
	@Autowired
	private AuthendicationService authservice;
	@Autowired
	private LoginValidatedService loginValidationComponent;
	@Autowired
	private PrintReqService reqPrinter;

	@PostMapping("/login")
	@ApiOperation(value="This method is to Create Token For Access Other Apis")
	public ResponseEntity<CommonLoginRes> getloginToken(@RequestBody LoginRequest mslogin, HttpServletRequest http)  {
		CommonLoginRes res = new CommonLoginRes();
		reqPrinter.reqPrint(mslogin);
		res =loginValidationComponent.loginInputValidation(mslogin); 
		if(res.getErrorMessage()!=null &&  res.getErrorMessage().size()>0 ) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.OK);
		} 
		
		res = authservice.checkUserLogin(mslogin,http);
		if(res.getCommonResponse() !=null) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
	}
	

	
}
