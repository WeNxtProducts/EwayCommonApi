package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaxInvoicePremiumDetails {

	@JsonProperty("Amount")
	private String amount;
	
	@JsonProperty("Narration")
	private String narration;
	
	@JsonProperty("Status")
	private String status;
	
}
