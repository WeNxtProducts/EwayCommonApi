package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.RiskDetailsGetRes;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class ProductRiskDetailsRes {
	
	@JsonProperty("RiskDetails")
	private  RiskDetailsGetRes riskDetails   ;
	
	@JsonProperty("DriverDetails")
	private  List<DriverDetailsRes> driverDetails   ;
	
	
	@JsonProperty("Covers")
	private  List<Cover> covers ;
}
