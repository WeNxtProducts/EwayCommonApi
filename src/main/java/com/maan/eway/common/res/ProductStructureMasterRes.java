package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ProductStructureMasterRes {
	
	@JsonProperty("SectionId")
	private Integer sectionid;

	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("Status")
	private String Status;
	
	@JsonProperty("IndustryType")
	private String IndustryType;
	
}
