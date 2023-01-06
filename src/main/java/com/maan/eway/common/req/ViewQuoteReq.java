package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewQuoteReq {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
}
