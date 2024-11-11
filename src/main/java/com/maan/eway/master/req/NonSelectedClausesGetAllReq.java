package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonSelectedClausesGetAllReq {


	    @JsonProperty("InsuranceId")
	    private String companyId;
	    
	    @JsonProperty("ProductId")
	    private String productId;
	    
	    @JsonProperty("BranchCode")
	    private String branchCode;
	    
	    @JsonProperty("SectionId")
	    private String sectionId;
	    
}
