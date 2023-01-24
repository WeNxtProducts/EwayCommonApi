package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortfolioCustomerDetailsRes {


    @JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
    
    @JsonProperty("RequestReferenceNo")
    private String   requestReferenceNo ;
	
    @JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("IdNumber")
	private String idNumber;
	
	@JsonProperty("MobileNo")
	private String mobileNo1;

	@JsonProperty("TaxExemptedId")
	private String taxExemptedId;

	@JsonProperty("IsTaxExempted")
	private String isTaxExempted;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date inceptionDate;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date expiryDate;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	  
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("OverallPremiumLc")
	private Double overallPremiumLc;
	
	@JsonProperty("OverallPremiumFc")
	private String overallPremiumFc;
	
	@JsonProperty("DebitAcNo")
	private String debitAcNo;

	@JsonProperty("DebitTo")
	private String debitTo;

	@JsonProperty("DebitToId")
	private String debitToId;

	@JsonProperty("DebitNoteNo")
	private String DebitNoteNo;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DebitNoteDate")
	private Date debitNoteDate;

	@JsonProperty("CreditTo")
	private String creditTo;

	@JsonProperty("CreditToId")
	private String creditToId;
	
	@JsonProperty("CreditNo")
	private String creditNo;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("CreditDate")
	private Date creditDate;
	
	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("Count")
	private String   count ;
	
	
}
