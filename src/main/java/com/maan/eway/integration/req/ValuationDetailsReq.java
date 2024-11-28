package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationDetailsReq {
	
	@JsonProperty("RecordId")
	private String recordId;
	
	@JsonProperty("ValCompanyId")
	private String valCompanyId;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
