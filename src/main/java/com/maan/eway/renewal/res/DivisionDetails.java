package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DivisionDetails {
	@JsonProperty("DivisionCode")
	private String divisionCode;

	@JsonProperty("DivisionName")
	private String divisionName;

	@JsonProperty("TotalPolicyCount")
	private String totalPolicycount;

	@JsonProperty("TotalPremium")
	private String totalPremium;
}
