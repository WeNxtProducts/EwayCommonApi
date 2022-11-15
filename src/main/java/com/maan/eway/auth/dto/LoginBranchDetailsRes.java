package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginBranchDetailsRes {
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BranchName")
	private String branchName ;
	
	@JsonProperty("RegionCode")
	private String regionCode ;
	
	@JsonProperty("RegionName")
	private String regionName ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	
	@JsonProperty("CompanyName")
	private String companyName ;
	
	@JsonProperty("CompanyLogo")
	private String companyLogo ;
	
	@JsonProperty("AttachedBranchCode")
	private String attachedBranchCode ;
	
	@JsonProperty("AttachedBranchName")
	private String attachedBranchName ;
	
	@JsonProperty("AttachedRegionCode")
	private String attachedRegionCode ;
	
	@JsonProperty("AttachedRegionName")
	private String attachedRegionName ;
	
	@JsonProperty("AttachedCompanyId")
	private String attachedCompanyId ;
	
	
	@JsonProperty("AttachedCompanyName")
	private String attachedCompanyName ;
	
	@JsonProperty("AttachedCompanyLogo")
	private String attachedCompanyLogo ;
	
	@JsonProperty("CurrencyId")
	private String currencyId;
}
