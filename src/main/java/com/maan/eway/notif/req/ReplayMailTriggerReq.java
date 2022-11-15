package com.maan.eway.notif.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReplayMailTriggerReq {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("UUID")
	private Long uuid;
	
	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;

}
