package com.maan.eway.notif.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MailMasterGetRes implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("SNo")
	private String SNo;

	@JsonProperty("CompanyId")
	private String companyId;

	@JsonProperty("SmtpHost")
	private String smtpHost;

	@JsonProperty("SmtpUser")
	private String smtpUser;

	@JsonProperty("SmtpPwd")
	private String smtpPwd;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("SmtpPort")
	private String smtpPort;	
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
		
	@JsonProperty("CreatedBy")
	private String createdBy;

	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date       entryDate    ;
	
	@JsonProperty("AmendId")
	private Integer amendId;

	
}
