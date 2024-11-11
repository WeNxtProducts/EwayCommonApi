package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ViewQuoteRes {

	@JsonProperty("QuoteDetails")
	private QuoteDetailsRes  quoteDetails ;
	
	@JsonProperty("CustomerDetails")
	private CustomerDetailsRes  customerDetails ;
	
	@JsonProperty("RiskDetails")
	private Object  riskDetails ;
	
	@JsonProperty("DocumentDetails")
	private List<DocumentDetails> documentDetails ;
	
	@JsonProperty("LocationDetails")
	private List<LocationDetailsRes> locationDetails ;
	
	 @JsonProperty("TotalAccessoriesSumInsured")
	 private Double totalAccessoriesSumInsured;
	
}
