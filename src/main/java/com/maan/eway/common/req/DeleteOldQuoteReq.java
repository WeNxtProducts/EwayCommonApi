package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DeleteOldQuoteReq {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("ProductId")
	private String productId;
	
}
