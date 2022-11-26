package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExclusionMasterGetallReq {

    @JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;

}
