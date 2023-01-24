package com.maan.eway.admin.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferalGridCriteriaRes {


	// Customer Info
	private Long   idsCount ;
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
	private Date entryDate;

}
