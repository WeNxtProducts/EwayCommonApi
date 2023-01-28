package com.maan.eway.master.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationDropDownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("CategoryId")
	private String categoryId;
	
	@JsonProperty("ProductId")
	private String productId;
	
}
