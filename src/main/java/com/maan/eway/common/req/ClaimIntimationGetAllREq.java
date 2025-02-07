package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.master.req.SectionCoverMasterGetAllReq;

import lombok.Data;
@Data
public class ClaimIntimationGetAllREq {

	 @JsonProperty("ProductId")
	 private String productId;
	 
	 @JsonProperty("CompanyId")
	 private String companyId;
}
