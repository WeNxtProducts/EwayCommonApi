package com.maan.eway.notif.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SmsMasterGetRes {
	
	@JsonProperty("SNo")
	private String sNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("SenderId")
	private String senderId;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("SmsUserPass")
	private String smsUserPass;
	
	@JsonProperty("SmsUserName")
	private String smsUserName;
	
	@JsonProperty("SmsPartyUrl")
	private String smsPartyUrl;	
	
	@JsonProperty("SecureYn")
	private String secureYn;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;


	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("AmendId")
	private String amendId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	 
}
