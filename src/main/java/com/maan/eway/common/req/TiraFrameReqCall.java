package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TiraFrameReqCall {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	@JsonProperty("RiskId")
	private String riskId;
	
}
