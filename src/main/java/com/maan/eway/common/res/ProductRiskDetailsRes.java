package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.CoverRes;
import com.maan.eway.res.RiskDetailsGetRes;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class ProductRiskDetailsRes {
	
	@JsonProperty("RiskId")
	private  String riskId;	
	
	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("SectionName")
	private  String sectionName;	
	
	@JsonProperty("RiskDetails")
	private  Object riskDetails   ;	
	
	
	@JsonProperty("Covers")
	private  List<CoverRes> covers ;
}
