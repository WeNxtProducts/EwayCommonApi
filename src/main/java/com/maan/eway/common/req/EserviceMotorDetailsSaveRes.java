package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class EserviceMotorDetailsSaveRes {

	@JsonProperty("CoverList")
	private List<Cover> coverList ;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
	@JsonProperty("MSRefNo")
	private String msrefno;
	
	 @JsonProperty("CdRefNo")
		private String cdRefNo;

		@JsonProperty("VdRefNo")
		private String vdRefNo;
		
	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	 
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
}
