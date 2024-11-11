package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IntegrationSaveRes {
	
	@JsonProperty("Response")
	private String response;

	@JsonProperty("ErrorMessage")
	private String errorMessage;
	
}
