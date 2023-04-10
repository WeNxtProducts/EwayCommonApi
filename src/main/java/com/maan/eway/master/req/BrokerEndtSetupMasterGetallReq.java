package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerEndtSetupMasterGetallReq {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
}
