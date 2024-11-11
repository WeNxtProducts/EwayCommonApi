package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateDropdown {

	@JsonProperty("Code")
	private String Code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	
	
	
	@JsonProperty("Status")
	private String status;
	
}
