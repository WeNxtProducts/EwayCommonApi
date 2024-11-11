package com.maan.eway.notification.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MailTemplateRes {


	@JsonProperty("MailSubject")
	private String mailSubject;

	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;
	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;


}
