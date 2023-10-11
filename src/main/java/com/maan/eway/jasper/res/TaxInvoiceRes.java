package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaxInvoiceRes {
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("ApprovedBy")
	private String approvedBy;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("VrTinNo")
	private String vrTinNo;
	
	@JsonProperty("CustomerTin")
	private String customerTin;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("DebitNoteNo")
	private String debitNoteNo;
	
	@JsonProperty("VrnNumber")
	private String vrnNumber;
	
	@JsonProperty("TinNumber")
	private String tinNumber;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("VatPremium")
	private String vatPremium;
	
	@JsonProperty("OverAllPremium")
	private String overAllPremium;
	
	@JsonProperty("TotSumInsured")
	private String totSumInsured;
	
	@JsonProperty("VatPercent")
	private String vatPercent;
	
	@JsonProperty("Dataset1List")
	private List<TaxDataSetOneRes> dataset1List;
	
}