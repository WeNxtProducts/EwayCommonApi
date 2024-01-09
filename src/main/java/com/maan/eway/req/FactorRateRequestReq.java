package com.maan.eway.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateRequestReq {
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

}
