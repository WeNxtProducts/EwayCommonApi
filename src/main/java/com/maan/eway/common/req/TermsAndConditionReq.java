package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionReq {

	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
}
