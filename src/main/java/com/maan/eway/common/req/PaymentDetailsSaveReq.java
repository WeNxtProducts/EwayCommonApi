package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentDetailsSaveReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
//	
//	@JsonProperty("Remarks")
//	private String remarks;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("EmiYn")
	private String emiYn;

}
