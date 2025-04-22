package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackAgentResByProduct {

	@JsonProperty("SourceCode")
	private String sourceCode;
	@JsonProperty("SourceName")
	private String sourceName;
	@JsonProperty("SourceCount")
	private String sourceCount;
	@JsonProperty("TotalPremium")
	private String totalPremium;
}
