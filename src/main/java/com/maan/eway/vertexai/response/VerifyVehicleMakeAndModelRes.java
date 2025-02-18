/**
 * @author : Ashok Kumar S 
 * @since  : 20-01-2025
 */
package com.maan.eway.vertexai.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VerifyVehicleMakeAndModelRes {
	
	//AI analysis
	@JsonProperty("ObjectType")
	private String objectType;
	
	@JsonProperty("GeneratedVehicleMake")
	private String generatedVehicleMake;
	
	@JsonProperty("GeneratedVehicleModel")
	private String generatedVehicleModel;
	
	//provided details
	@JsonProperty("ProvidedVehicleMake")
	private String providedVehicleMake;
	
	@JsonProperty("ProvidedVehicleModel")
	private String providedVehicleModel;
	
	//Matching Result
	@JsonProperty("IsVehicleMakeMatched")
	@JsonFormat(shape = Shape.STRING)
	private Boolean isVehicleMakeMatched;
	
	@JsonProperty("IsVehicleModelMatched")
	@JsonFormat(shape = Shape.STRING)
	private Boolean isVehicleModelMatched;

	@JsonProperty("Disclaimer")
	private String disclaimer;
}
