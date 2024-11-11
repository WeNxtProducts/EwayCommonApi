package com.maan.eway.admin.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferalGridCriteriaRes {


	// Customer Info
	//private Long   idsCount ;
    private String   customerReferenceNo ;
    private String idNumber;
	private String clientName;

	// Vehicle Info
	private String     companyId ;
	private String     productId ;
	private String     branchCode ;
	
	private String   requestReferenceNo ;
	private String quoteNo;
	private String customerId;
	private Date policyStartDate;
	private Date policyEndDate;
	private String rejectReason;
	private String adminRemarks;
	private String referalRemarks;
	
	private Integer endorsementType;
	private String endorsementTypeDesc;
	private Date endorsementDate;
	private String endorsementRemarks;
	private Date endorsementEffdate;
	private String originalPolicyNo;
	private String endtPrevPolicyNo;
	private String endtPrevQuoteNo;
	private BigDecimal endtCount;
	private String endtStatus;
	private String endtCategDesc;
	private Double endtPremium;

	

}
