package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class GetMotorProtfolioPendingRes {

	@JsonProperty("Count")
	private Long   count ;

	@JsonProperty("Portfolio")
    private List<PortfolioPendingGridCriteriaRes> pending;
	
}
