package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortfolioGridRes {
	
	   @JsonProperty("CreditNo")
	   private String     creditNo; 
	   
	   @JsonProperty("DebitNo")
	   private String     debitNo; 

	   @JsonProperty("ApplicationId")
	   private String     applicationId;
	   
	   @JsonProperty("BrokerName")
	   private String     brokerName;
	   
	   @JsonProperty("CustomerName")
	   private String     customerName;
	   
	   @JsonProperty("BrokerCode")
	   private String     brokerCode;
	   
	   @JsonProperty("BrokerLoginId")
	   private String     brokerLoginId;
	   
	   @JsonProperty("UserType")
	   private String     userType;
	   
	   @JsonProperty("ExchangeRate")
	   private String     exchangeRate;
	   
	   @JsonProperty("SubUserType")
	   private String     subUserType;
	   
	   @JsonProperty("Count")
	   private Long     count;
	    
	   @JsonProperty("OverallPremiumLc")
	   private String     overallPremiumLc;
	   
	   @JsonProperty("OveralPremiumFc")
	   private String     overallPremiumFc;
	   
	   @JsonProperty("CurrencyCode")
	   private String     currencyCode;
	   
	   @JsonProperty("RequestReferenceNo")
	   private String     requestReferenceNo;
	   
	   @JsonProperty("QuoteNo")
	   private String     quoteNo;
	   
	   @JsonProperty("PolicyNo")
	   private String     policyNo;
	   
	   @JsonProperty("OriginalPolicyNo")
	   private String     originalPolicyNo;
	   
	   @JsonProperty("PolicyStartDate")
	   private String     policyStartDate;
	   
	   @JsonProperty("PolicyEndDate")
	   private String     policyEndDate;
	   
	   @JsonProperty("ProductId")
	   private String     productId;
	   
	   @JsonProperty("ProductName")
	   private String     productName;
	  
	   @JsonProperty("AdminRemarks")
	   private String     adminRemarks;
	   
	   @JsonProperty("ReferralRemarks")
	   private String     referralRemarks;
	   
	   @JsonProperty("Remarks")
	   private String     remarks;
	   
	   @JsonProperty("AdminLoginId")
	   private String     adminLoginId;
	   
	   @JsonProperty("Status")
	   private String     status;
	   
	   @JsonProperty("EndtSatus")
	   private String     endtSatus;
	   
	   @JsonProperty("StatusDesc")
	   private String     statusDesc;
	   
	   @JsonProperty("EndtStatusDesc")
	   private String     endtStatusDesc;
	   
	   @JsonProperty("BranchCode")
	   private String     BranchCode; 
	   
	   @JsonProperty("BranchName")
	   private String     branchName; 
	   
	   @JsonProperty("BrokerBranchCode")
	   private String     brokerBranchCode; 
	   
	   @JsonProperty("BrokerBranchName")
	   private String     brokerBranchName; 
	
	   @JsonProperty("UpdatedDate")
	   private String     updatedDate; 
	   
	   @JsonProperty("EndorsementRemarks")
	   private String     endorsementRemarks; 
	   

}
