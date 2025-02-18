/**
 * @author : Ashok Kumar S 
 * @since  : 20-01-2025
 */
package com.maan.eway.vertexai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.vertexai.request.VerifyVehicleMakeAndModelReq;
import com.maan.eway.vertexai.response.VerifyVehicleMakeAndModelRes;
import com.maan.eway.vertexai.serviceimpl.VerifyVehicleMakeAndModelServiceImpl;

@RestController
@RequestMapping("/vertexai")
public class VerifyVehicleMakeAndModelController {
	
	private final VerifyVehicleMakeAndModelServiceImpl verifyMakeModelService;
	
	@Autowired
	public VerifyVehicleMakeAndModelController(VerifyVehicleMakeAndModelServiceImpl verifyMakeModelService) {
		this.verifyMakeModelService = verifyMakeModelService;
	}
	
	
	/**
	 * Verifies the vehicle's make and model using AI image analysis.
	 *
	 * @param req the request object containing vehicle make and model details
	 * @return ResponseEntity containing the verification result, with appropriate HTTP status codes:
	 *         - 200 OK if AI analysis is successful
	 *         - 400 Bad Request if an exception occurs during AI analysis
	 *         - 422 Unprocessable Entity if the request validation fails or AI analysis returns an error
	 */
	@PostMapping("/verifymakeandmodel")
	public ResponseEntity<CommonRes> verifyVehicleMakeAndModel(@RequestBody VerifyVehicleMakeAndModelReq req){
		CommonRes response = new CommonRes();

		// Validate the incoming request for vehicle make and model details
		List<Error> errors = verifyMakeModelService.validateVehicleMakeAndModelRequest(req);
		if(!errors.isEmpty()) {
			response.setMessage("Failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
	    // Perform AI analysis to verify the vehicle's make and model
		Object aiResult = verifyMakeModelService.checkSavedMakeAndModelWithAIImageAnalysis(req);

	    // If exception occured during AI analysis failed, return a bad request response
		if(aiResult == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
	    // If AI analysis returns an error, return a failed response with the error details
		if(aiResult instanceof Error) {
			Error error = (Error) aiResult;
			response.setMessage("Failed");
			response.setIsError(true);
			response.setErrorMessage(List.of(error));
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		
	    // If AI analysis is successful
		VerifyVehicleMakeAndModelRes verifyResponse = (VerifyVehicleMakeAndModelRes) aiResult;			
		response.setMessage("Success");
		response.setIsError(false);
		response.setCommonResponse(verifyResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);			
	}
	
	
}