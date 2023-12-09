package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteFidelityRes {
	
	@JsonProperty("FidEmpSi")
	private BigDecimal fidEmpSi;
	
	
	@JsonProperty("FidEmpCount")
	private BigDecimal fidEmpCount;
	
}
