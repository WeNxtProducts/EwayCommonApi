package com.maan.eway.notif.req;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MailFramingReq {

	@JsonProperty("NotifTemplateId")
	private String notifTemplateId ;
	
	@JsonProperty("InsuanceId")
	private String insId ;
	
	@JsonProperty("MailCc")
	private List<String> mailCc ;
	
	@JsonProperty("MailTo")
	private  List<String> mailTo ;
	
	@JsonProperty("Keys")
	private Map<String,Object> keys ;
	
	@JsonProperty("MailRegards")
	private String mailRegards ;
	
	@JsonProperty("Status")
	private String status ;
	
}
