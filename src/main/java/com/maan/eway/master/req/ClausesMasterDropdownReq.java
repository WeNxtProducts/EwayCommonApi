package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClausesMasterDropdownReq {

	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
//	
//	@JsonProperty("PolicyType")
//	private String policyType;
	
}
