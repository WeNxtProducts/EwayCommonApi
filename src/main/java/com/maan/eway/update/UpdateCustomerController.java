package com.maan.eway.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.res.SuccessRes;

@RestController
@RequestMapping("/tira")
public class UpdateCustomerController {

	 @Autowired
	    private UpdateCustomerService customerService;  // Assumed service that handles business logic

	    @PostMapping("/updatecustomer")
	    public ResponseEntity<SuccessRes> updateCustomerDetails(@RequestBody EserviceCustomerSaveReq req) {
	        // Call the service layer to update customer details
	        SuccessRes response = customerService.updateCustomerDetails(req);

	        // Return the response as a JSON object with HTTP status 200 (OK)
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	
}
