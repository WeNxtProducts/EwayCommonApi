package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiTransactionDetailsRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("PremiumWithTax")
	private String premiumWithTax;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

	@JsonProperty("InstallmentDesc")
	private String     installmentDesc ;
	
	@JsonProperty("Interest")
	private String interest;
	
	@JsonProperty("Advance")
	private String advance;

	@JsonProperty("NoOfInstallment")
	private String installment;
	
	@JsonProperty("InterestAmount")
	private String interestAmount;
	
	@JsonProperty("DueAmount")
	private String dueAmount;

	@JsonProperty("BalanceAmount")
	private String balanceAmount;

	@JsonProperty("TotalLoanAmount")
	private String totalLoanAmount;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("DueDate")
	private Date dueDate;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PaymentDate")
	private Date paymentDate;

	@JsonProperty("PaymentStatus")
	private String paymentStatus;
	
	@JsonProperty("PaymentDetails")
    private String     paymentDetails ;
	
}
