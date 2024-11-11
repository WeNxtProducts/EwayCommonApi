package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.CoverRes;
import com.maan.eway.res.RiskDetailsGetRes;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class ProductRiskDetailsRes {
	
	
	@JsonProperty("RiskDetails")
	private  Object riskDetails   ;	
	
}
