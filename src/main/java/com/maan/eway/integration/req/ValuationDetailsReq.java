package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationDetailsReq {
private static final long serialVersionUID = 1L;
	
	@JsonProperty("RecordId")
	private String recordId;
}
