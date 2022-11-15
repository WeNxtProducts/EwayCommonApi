package com.maan.eway.notif.res;

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
public class NotifTemplateMasterRes implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("Sno")
	private String sno;

	@JsonProperty("InsuranceId")
	private String insId;

	@JsonProperty("MailRequired")
	private String mailRequired;

	@JsonProperty("MailSubject")
	private String mailSubject;

	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;

	@JsonProperty("SmsRequired")
	private String smsRequired;

	@JsonProperty("SmsSubject")
	private String smsSubject;

	@JsonProperty("SmsBodyEn")
	private String smsBodyEn;

	@JsonProperty("SmsRegards")
	private String smsRegards;

	@JsonProperty("WhatsappRequired")
	private String whatsappRequired;

	@JsonProperty("WhatsappSubject")
	private String whatsappSubject;

	@JsonProperty("WhatsappBodyEn")
	private String whatsappBodyEn;

	@JsonProperty("WhatsappRegards")
	private String whatsappRegards;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private String entryDate;

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
