package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerQuoteReq {


	   @JsonProperty("BranchCode")
	   private String     branchCode ;
	   @JsonProperty("InsuranceId")
	   private String     insuranceId ;
	   @JsonProperty("CreatedBy")
	   private String     createdBy;
	   @JsonProperty("LoginId")
	   private String     loginId;
	   @JsonProperty("ApplicationId")
	   private String     applicationId;
	   
	   @JsonProperty("SourceType")
	   private String     sourceType;
	  
	   @JsonProperty("UserType")
	   private String     userType;
	   @JsonProperty("SubUserType")
	   private String     subUserType;
	   @JsonProperty("ProductId")
	   private String     productId     ;
	   @JsonProperty("Limit")
	   private String     limit     ;
	   @JsonProperty("Offset")
	   private String     offset ;
}
