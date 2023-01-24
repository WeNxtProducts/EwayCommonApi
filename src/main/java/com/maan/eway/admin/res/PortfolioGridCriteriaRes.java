package com.maan.eway.admin.res;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioGridCriteriaRes {


	// Customer Info
	private Long   idsCount ;
	private String customerReferenceNo;
	private String idNumber;
	private String clientName;
	private String mobileNo1;
	private String taxExemptedId;
	private String isTaxExempted;
	

	// Vehicle Info
	private String     companyId ;
	private int     productId ;
	private String     branchCode ;
	
	private String   requestReferenceNo ;
	private String quoteNo;
	private String customerId;
	private Date inceptionDate;
	private Date expiryDate;
	private Double overallPremiumLc;
	private Double overallPremiumFc;
	private String policyNo;
	private String debitAcNo;
	private String debitTo;
	private String debitToId;

	private String debitNoteNo;

	private Date debitNoteDate;

	private String creditTo;

	private String creditToId;

	private String creditNo;

	private Date creditDate;
	
	private String emiYn;

	private String installmentPeriod;
	
	private Date effectiveDate;
	
	private String currency;
}

