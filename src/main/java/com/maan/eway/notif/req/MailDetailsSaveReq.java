package com.maan.eway.notif.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MailDetailsSaveReq {

	@JsonProperty("MailSubject")
	private String mailSubject;
	
	@JsonProperty("MailBody")
	private String mailBody;
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	

	@JsonProperty("InsCompanyId")
	private String insCompanyId;
	

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("Sender")
	private String sender;

	@JsonProperty("UserType")
	private String userType;
	

	

}
