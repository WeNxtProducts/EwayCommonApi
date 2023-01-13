package com.maan.eway.master.req;

import java.util.Date;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClausesMasterReq {

	@JsonProperty("ClausesId")
	private String clausesId;
	
	@JsonProperty("ClausesDescription")
	private String clausesDescription;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("ExtraCoverId")
	private String extraCoverId;
	
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
//	
//	@JsonProperty("PolicyType")
//	private String policyType;


}
