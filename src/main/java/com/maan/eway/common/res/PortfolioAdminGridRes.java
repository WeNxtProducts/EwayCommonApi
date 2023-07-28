package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAdminGridRes {

	   private String     applicationId;
	   private Long     count;
	   private BigDecimal     overallPremiumLc;
	   private BigDecimal     overallPremiumFc;
	   private String currencyCode ; 
	   private BigDecimal exchangeRate ; 
	   private String requestReferenceNo ; 
	   private String quoteNo ; 
	   private String policyNo ; 
	   private String originalPolicyNo ; 
	   private Integer productId ; 
	   private String productName ; 
	   private Integer oaCode ;
	   private String     loginId;
	   private String remarks ;
	   private String     adminRemarks;
	   private String     referralRemarks;
	   private String     adminLoginId;
	   private String     status;
	   private String     endtStatus;
	   private String     customerName;
	   private Date     policyStartDate;
	   private Date     policyEndDate;
	   private String     branchCode; 
	   private String     branchName; 
	   private String     brokerBranchCode; 
	   private String     brokerBranchName; 
	   private String     brokerName; 
	   private String     userType;
	   private String     subUserType;
//		h.get("quoteNo").alias("quoteNo") ,
//		h.get("policyNo").alias("policyNo") ,
//		h.get("productId").alias("productId") ,
//		h.get("productName").alias("productName") ,
//		h.get("agencyCode").alias("oaCode") ,
//		h.get("loginId").alias("loginId") ,
//		h.get("remarks").alias("remarks") ,
//		h.get("referralDescription").alias("referralRemarks") ,
//		h.get("adminRemarks").alias("adminRemarks") ,
//		h.get("adminLoginId").alias("adminLoginId"),
//		h.get("status").alias("status"),
//		h.get("endtStatus").alias("endtStatus"),
//		h.get("customerName").alias("customerName") ,
//		h.get("inceptionDate").alias("policyStartDate") ,
//		h.get("expiryDate").alias("policyEndDate") ,
//		h.get("branchCode").alias("branchCode") ,
//		h.get("branchName").alias("branchName") ,
//		h.get("brokerBranchCode").alias("brokerBranchCode") ,
//		h.get("brokerBranchName").alias("brokerBranchName") ,
//		u.get("userName").alias("brokerName") ,
//		l.get("userType").alias("userType") ,
//		l.get("subUserType").alias("subUserType")
	
	
}
