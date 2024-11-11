package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.GetAllMotorDetailsRes;

import lombok.Data;

@Data
public class CopyQuoteSuccessRes {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("Eservice")
	private Object commonResponse;
	
	
}
