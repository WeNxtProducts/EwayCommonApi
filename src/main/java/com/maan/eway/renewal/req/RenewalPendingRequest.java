package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalPendingRequest {

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("TranId")
    private String tranId;

}
