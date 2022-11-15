package com.maan.eway.notif.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReadMailReq {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("FromEmail")
	private String fromEmail ;
	
	@JsonProperty("SubjectMatch")
	private String subject ;
	
}
