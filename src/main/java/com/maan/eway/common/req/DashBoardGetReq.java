package com.maan.eway.common.req;

import java.util.Date;

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
	   private Date     startDate     ;
	   @JsonProperty("EndDate")
	   private Date     endDate ;
}
