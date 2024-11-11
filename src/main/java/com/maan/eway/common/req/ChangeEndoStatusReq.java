package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
public class ChangeEndoStatusReq {

	
	@JsonProperty("QuoteNo")
	private String quoteNo;
		
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
}
