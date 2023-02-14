package com.maan.eway.notification.req;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class Mail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * MailTo
		 MailSubject
		 MailBody		
		 MailRegards
	 */
	
	private String mailTo;
	private String mailSubject;
	private String mailBody;
	private String mailRegards;
	private List<String> mailcc;
	private JobCredentials credential;
	private String attachments;
}
