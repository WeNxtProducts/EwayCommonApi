package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentDetailsSaveReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("PaymentType")
	private String paymentType;
	
	@JsonProperty("ShortenUrl")
	private String shortenUrl;
	
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
	
	@JsonProperty("BankName")
	private String bankName;

	@JsonProperty("ChequeNo")
	private String chequeNo;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ChequeDate")
	private Date chequeDate;
	
}
