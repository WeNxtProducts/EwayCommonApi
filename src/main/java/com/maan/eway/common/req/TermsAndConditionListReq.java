package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionListReq {

	@JsonProperty("SubId")
	private String subId;
	
	
}
