/**
 * @author : Ashok Kumar S 
 * @since  : 20-01-2025
 */
package com.maan.eway.vertexai.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VerifyVehicleMakeAndModelReq {
	//Fields related to get uploaded image
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("UniqueId")
	private String uniqueId;
	
	// Fields related to get unique record in eservice motor details
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("RiskId")
	@JsonFormat(shape = Shape.STRING)
	private Integer riskId;

}
