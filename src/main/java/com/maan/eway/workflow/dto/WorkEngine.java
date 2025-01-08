package com.maan.eway.workflow.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maan.eway.workflow.util.LocalDateTimeTypeAdapter;

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
	
		@JsonProperty("QuoteNo")
		private String quoteNo;
		
		@JsonProperty("Integ_Type")
		private String integType;
		
		@JsonProperty("PolicyNo")
		private String policyNo;
		
		@Override
		public String toString() {
			 Gson gson = new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) .create();
			return gson.toJson(this);
		}
		
}
