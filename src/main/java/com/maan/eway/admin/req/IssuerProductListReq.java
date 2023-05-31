package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerProductListReq {

	@JsonProperty("ProductId")
	private String productId ;
	@JsonProperty("SuminsuredStart")
	private String  suminsuredStart; 
	
	@JsonProperty("SuminsuredEnd")
	private String  suminsuredEnd; 
	
	
	@JsonProperty("ReferralIds")
	private List<String> referralIds ;
	
	@JsonProperty("EndorsementIds")
	private List<String> endorsementIds ;
	
	@JsonProperty("ColumnName")
	private String  columnName; 
	
}
