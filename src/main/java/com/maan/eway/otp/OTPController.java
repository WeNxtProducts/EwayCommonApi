package com.maan.eway.otp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.otp.dto.OtpConfirm;
import com.maan.eway.otp.dto.UserOtp;
import com.maan.eway.otp.dto.ValidateOtp;
import com.maan.eway.otp.service.OTPService;

import io.swagger.annotations.ApiOperation;




@RestController
@RequestMapping("/otp")
public class OTPController {
	
	@Autowired
	private OTPService service;
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/generate")
	//@PostMapping(value="/generate", consumes =MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<Object> generate(@RequestBody UserOtp otp){
		OtpConfirm data=service.generate(otp);
		if (data != null) {
			return new ResponseEntity<Object>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	} 
	//@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/validate")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<Object> validate(@RequestBody ValidateOtp otp){
		OtpConfirm data=service.validate(otp);
		if (data != null) {
			return new ResponseEntity<Object>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	} 
	
	
	@PostMapping("/createLogin")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<Object> createLogin(@RequestBody ValidateOtp otp){
		OtpConfirm data=service.createUser(otp);
		if (data != null) {
			return new ResponseEntity<Object>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	} 
}
