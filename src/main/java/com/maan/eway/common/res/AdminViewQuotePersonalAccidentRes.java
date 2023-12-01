package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuotePersonalAccidentRes {

	@JsonProperty("OccupationType")
	private String occupationType;
	
	@JsonProperty("SumInsured")
	private BigDecimal sumInsured;
	
}
