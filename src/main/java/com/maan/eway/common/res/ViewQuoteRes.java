package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ViewQuoteRes {

	@JsonProperty("QuoteDetails")
	private QuoteDetailsRes  quoteDetails ;
	
	@JsonProperty("CustomerDetails")
	private CustomerDetailsRes  customerDetails ;
	
	@JsonProperty("ProductDetails")
	private Object  productDetails ;
}
