package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackReq {
	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("DivisionCode")
	private String divisionCode;

	@JsonProperty("ProductCode")
	private String productCode;

	@JsonProperty("SourceCode")
	private String sourceCode;
}
