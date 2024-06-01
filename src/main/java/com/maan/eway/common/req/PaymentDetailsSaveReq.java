package com.maan.eway.common.req;

import java.math.BigDecimal;
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
	
	@JsonProperty("BankCode")
	private String bankCode;
	
	@JsonProperty("BankName")
	private String bankName;

	@JsonProperty("ChequeNo")
	private String chequeNo;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("ChequeDate")
	private Date chequeDate;
	
	@JsonProperty("Payments")
	private String payments;
	
	@JsonProperty("AccountNumber")
	private String accountNumber;
	
	@JsonProperty("IbanNumber")
	private String ibanNumber;
	
	@JsonProperty("MICRNo")
	private String micrNo;
	@JsonProperty("PayeeName")
	private String payeeName;
	
	
	@JsonProperty("Premium")
	private BigDecimal premium;

	@JsonProperty("EmiYn")
	private String emiYn;
	

	@JsonProperty("WhatsappCode")
	private String whatsappCode;
	@JsonProperty("WhatsappNo")
	private String whatsappNo;
	
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	@JsonProperty("MobileCode1")
	private String mobileCode1;
}
