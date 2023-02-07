package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CopyQuoteReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId;

	@JsonProperty("SearchKey")
	private String searchKey;

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

//	@JsonProperty("BrokerBranchCode")
//	private String brokerBranchCode;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SearchValue")
	private String searchValue;


	
}
