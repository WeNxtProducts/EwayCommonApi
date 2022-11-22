package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExistingQuoteReq {


	   @JsonProperty("BranchCode")
	   private String     branchCode ;
	   @JsonProperty("InsuranceId")
	   private String     insuranceId     ;
	   @JsonProperty("CreatedBy")
	   private String     createdBy;
	   @JsonProperty("ProductId")
	   private String     productId     ;
	   @JsonProperty("Limit")
	   private String     limit     ;
	   @JsonProperty("Offset")
	   private String     offset ;
}
