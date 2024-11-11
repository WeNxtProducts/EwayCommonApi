package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data

public class PremiaConfigDataMasterGetallReq {
	@JsonProperty("PremiaId")
	private String premiaId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	

}
