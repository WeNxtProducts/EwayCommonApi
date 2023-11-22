package com.maan.eway.embedded;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.embedded.request.ClaimDetailsReq;
import com.maan.eway.embedded.request.Inalipa;
import com.maan.eway.embedded.response.InalipaDetailsRes;
import com.maan.eway.embedded.response.ResponseForInalipa;
import com.maan.eway.embedded.service.EmbeddedService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/embedded")
public class EmbeddedController {
	
	@Autowired
	private EmbeddedService embService;
	
	@PostMapping("/create/{LoginId}/policy")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<ResponseForInalipa>  createPolicy(@PathVariable("LoginId")  String loginId, @RequestBody Inalipa request) {
		ResponseForInalipa response=embService.createPolicy(loginId,request);
		if(response!=null)
			return new ResponseEntity<>(response,HttpStatus.OK);
		else
			return new ResponseEntity<>(null,HttpStatus.EXPECTATION_FAILED);	
		
	}
	
	@PostMapping("/create/{LoginId}/schedule/{EncodedPolicyNo}")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<ResponseForInalipa>  createSchedule(@PathVariable("LoginId")  String loginId,@PathVariable("EncodedPolicyNo") String encodedPolicyNo) {
		ResponseForInalipa response=embService.createSchedule(loginId,encodedPolicyNo);
		if(response!=null)
			return new ResponseEntity<>(response,HttpStatus.OK);
		else
			return new ResponseEntity<>(null,HttpStatus.EXPECTATION_FAILED);	
		
	}
	
	@PostMapping("getClaimDetails")
	public InalipaDetailsRes getClaimDetails(@RequestBody ClaimDetailsReq req){
		return embService.getClaimDetails(req);
	}
}
