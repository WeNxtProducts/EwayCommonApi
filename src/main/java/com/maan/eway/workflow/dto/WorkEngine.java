package com.maan.eway.workflow.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.MsVehicleDetails;

import lombok.Data;

@Data
public class WorkEngine {
	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("SectionId") 
	private String sectionId;
	@JsonProperty("VehicleId") 
	private String vehicleId;  
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("LocationId") 
	private String locationId; 

	 @JsonProperty("MSRefNo") 
	 private String msrefno;
	  
	 @JsonProperty("CdRefNo")
		private String cdRefNo;

		@JsonProperty("VdRefNo")
		private String vdRefNo;
		@JsonProperty("CreatedBy")
		private String createdBy;
		

}
