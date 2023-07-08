package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndtSectionsRes {

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName ;
	
	@JsonProperty("ProductType")
	private String productType;
}
