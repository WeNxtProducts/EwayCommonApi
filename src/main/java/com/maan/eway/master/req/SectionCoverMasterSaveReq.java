package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionCoverMasterSaveReq {

	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	
}
