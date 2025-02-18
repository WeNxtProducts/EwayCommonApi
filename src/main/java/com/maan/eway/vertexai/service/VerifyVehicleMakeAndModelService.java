/**
 * @author : Ashok Kumar S 
 * @since  : 20-01-2025
 */
package com.maan.eway.vertexai.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.vertexai.request.VerifyVehicleMakeAndModelReq;

public interface VerifyVehicleMakeAndModelService {
	
	public List<Error> validateVehicleMakeAndModelRequest (VerifyVehicleMakeAndModelReq req);
	
	public Object checkSavedMakeAndModelWithAIImageAnalysis(VerifyVehicleMakeAndModelReq req);
	
}
