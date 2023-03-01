package com.maan.eway.integration.req;

 
 

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PremiaRequest {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	
	
}
