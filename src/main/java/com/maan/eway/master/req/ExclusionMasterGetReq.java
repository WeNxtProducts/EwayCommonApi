package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExclusionMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("ExclusionId")
    private String    exclusionId     ;
    
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	
	@JsonProperty("SectionId")
	private String sectionId;
	
}
