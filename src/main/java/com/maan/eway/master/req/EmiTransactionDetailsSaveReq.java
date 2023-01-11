package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiTransactionDetailsSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("PremiumWithTax")
	private String premiumWithTax;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;
	
	@JsonProperty("PaymentDetails")
	private String paymentDetails;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("PolicyType")
	private String policyType;

//	@JsonProperty("InterestPercent")
//	private String interestPercent;
//
//	@JsonProperty("AdvancePercent")
//	private String advancePercent;

//	@JsonProperty("PaymentStatus")
//	private String paymentStatus;
	

	
}
