package com.maan.eway.req.calcengine;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class ReferralApi {

	@JsonProperty("InsuranceId") 
	private String insuranceId;

	@JsonProperty("ProductId") 
	private String productId;
	
	@JsonProperty("SumInsured")
	private String suminsured;
	
	@JsonProperty("BranchCode") 
	private String branchCode;
}
