package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MakePaymentSaveReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentTypeId")
	private String paymentTypeId;
	
}
