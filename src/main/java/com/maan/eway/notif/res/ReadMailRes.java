package com.maan.eway.notif.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReadMailRes {
	
	@JsonProperty("Subject")
	private String subject;
	
	@JsonProperty("From")
	private String from ;
	
	@JsonProperty("MailBody")
	private String mailBody;
	
	@JsonProperty("ReceivedDate")
	private Date receivedDate;
	
	@JsonProperty("MessageId")
	private Long MessageId;
	
}
