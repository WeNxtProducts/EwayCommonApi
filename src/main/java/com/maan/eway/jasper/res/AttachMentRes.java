package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttachMentRes {

	@JsonProperty("DocRefNo")
	private String docRefNo;
	
	@JsonProperty("DocLocation")
	private String docloction;
	
}
