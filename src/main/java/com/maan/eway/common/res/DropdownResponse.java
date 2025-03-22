package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DropdownResponse {

	@JsonProperty("LoctionId")
	private String loctionId;
	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;

}
