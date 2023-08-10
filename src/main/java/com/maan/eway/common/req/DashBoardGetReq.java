package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DashBoardGetReq {



	   @JsonProperty("InsuranceId")
	   private String     insuranceId     ;
	   @JsonProperty("BranchCode")
	   private String     branchCode     ;
	   @JsonProperty("ProductId")
	   private String     productId     ;
	   @JsonProperty("LoginId")
	   private String     loginId     ;
	   @JsonProperty("UserType")
	   private String     userType     ;
	   @JsonProperty("SubUserType")
	   private String     subUserType     ;
	   @JsonProperty("StartDate")
	   private String     startDate     ;
	   @JsonProperty("EndDate")
	   private String     endDate ;
}
