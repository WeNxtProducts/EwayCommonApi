package com.maan.eway.notification.req;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class Sms implements Serializable {

	/*
	 * SmsTo
		SmsSubject
		SmsBody	
		SmsRegards
	 */
	
	private String smsTo;
	private String smsSubject;
	private String smsBody;
	private String smsRegards;
	private String smsFrom;

}
