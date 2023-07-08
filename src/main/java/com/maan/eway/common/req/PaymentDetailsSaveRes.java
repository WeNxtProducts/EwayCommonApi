package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;

import lombok.Data;

@Data
public class PaymentDetailsSaveRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("Response")
	private String response ;
	
	@JsonProperty("MerchantReference")
	private String merchantReference;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("DebitNoteNo")
	private String debitNoteNo;
	
	@JsonProperty("CreditNoteNo")
	private String creditNoteNo;
	
	@JsonProperty("JsonObject")
	private JsonObject payment;
}
