package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorDescListRes {

	@JsonProperty("ErrorCode")
	private String errorCode;
	
	@JsonProperty("ErrorDesc")
	private String errorDesc;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
