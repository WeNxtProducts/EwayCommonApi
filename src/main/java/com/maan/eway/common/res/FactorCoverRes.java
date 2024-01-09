package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorCoverRes {
	
	@JsonProperty("Cover")
	private List<FactorRateRequestCommonRes> cover;

}
