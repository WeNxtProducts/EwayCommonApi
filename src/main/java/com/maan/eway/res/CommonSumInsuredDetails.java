package com.maan.eway.res;

import java.util.List;

import javax.annotation.sql.DataSourceDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommonSumInsuredDetails {

	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("SumInsured")
    private String     sumInsured ;
	

	@JsonProperty("CurrencyId")
    private String   currencyId ;
	
	@JsonProperty("SectionId")
	private List<String> sectionId;
}
