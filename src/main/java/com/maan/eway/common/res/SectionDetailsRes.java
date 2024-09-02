package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionDetailsRes {
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("Count")
	private String count;
	
	@JsonProperty("OccupationId")
	private String occupationId;
	
	@JsonProperty("OccupationDesc")
	private String occupationDesc;
	
	@JsonProperty("SumInsured")
	private String sumInsured;

	@JsonProperty("ContentType")
	private String contentType;
	
	@JsonProperty("ContentDesc")
	private String contentDesc;
	
	
}
