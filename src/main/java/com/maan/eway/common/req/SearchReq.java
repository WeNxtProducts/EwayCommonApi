package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
public class SearchReq {
	
	@JsonProperty("SearchKey")
	private String searchKey;

	@JsonProperty("SearchValue")
	private String searchValue;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("ApplicationId")
	private String applicationId;

	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
}
