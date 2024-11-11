package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductReferalMultiInsertReq {

	@JsonProperty("ReferalId")
	private String referalId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;
		
	@JsonProperty("CreatedBy")
	private String createdBy;
	
}
