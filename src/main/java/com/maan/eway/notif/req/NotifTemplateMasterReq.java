package com.maan.eway.notif.req;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Data;

@Data
public class NotifTemplateMasterReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("InsuranceId")
	private String insId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("QueryKey")
	private String queryKey;

	@JsonProperty("MailRequired")
	private String mailRequired;

	@JsonProperty("MailSubject")
	private String mailSubject;

	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;

	@JsonProperty("ModelNameEn")
	private String modelNameEn;

	@JsonProperty("MailBodyAr")
	private String mailBodyAr;

	@JsonProperty("MailRegardsAr")
	private String mailRegardsAr;

	@JsonProperty("SmsRequired")
	private String smsRequired;

	@JsonProperty("SmsSubject")
	private String smsSubject;

	@JsonProperty("SmsBodyEn")
	private String smsBodyEn;

	@JsonProperty("SmsBodyAr")
	private String smsBodyAr;

	@JsonProperty("SmsRegards")
	private String smsRegards;

	@JsonProperty("SmsRegardsAr")
	private String smsRegardsAr;

	@JsonProperty("WhatsappRequired")
	private String whatsappRequired;

	@JsonProperty("WhatsappSubject")
	private String whatsappSubject;

	@JsonProperty("WhatsappBodyEn")
	private String whatsappBodyEn;

	@JsonProperty("WhatsappBodyAr")
	private String whatsappBodyAr;

	@JsonProperty("WhatsappRegards")
	private String whatsappRegards;

	@JsonProperty("WhatsappRegardsAr")
	private String whatsappRegardsAr;

	@JsonProperty("NotificationApplicable")
	private String notificationApplicable;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
    private String     createdBy ;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
    
}
