package com.maan.eway.common.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionInsertReq {

	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	
	@JsonProperty("RiskId")
	private String riskId;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	
	@JsonProperty("TermsAndConditionReq")
	private List<TermsAndConditionListReq> termsAndConditionReq;
	
}
