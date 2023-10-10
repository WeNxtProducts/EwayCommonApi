package com.maan.eway.common.res;

import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchCustomerDetailsRes {
	
	@JsonProperty("CustomerName")
	private String clientName;

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	
//	@JsonProperty("CivilId")
//	private String civilId;

	@JsonProperty("ApplicationId")
	private String applicationId;

	@JsonProperty("ApproverName")
	private String approverName;
	
	@JsonProperty("MobileCode1")
	private String     mobileCodeDesc1 ;
	
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	
	@JsonProperty("Email1")
	private String email1;
	
	@JsonProperty("Occupation")
	private String occupationDesc;
	
	@JsonProperty("CityName")
	private String cityName;
	
	@JsonProperty("Address1")
	private String address1;
	
	
	@JsonProperty("Nationality")
	private String nationality;
	
	@JsonProperty("Division")
	private String branchCode;

	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("CustomerCodeName")
	private String customerName;
	
	@JsonProperty("IdType")
	private String idType;

	@JsonProperty("IdNumber")
	private String idNumber;

//	@JsonProperty("CustomerCodeName")
//	private String customerCodeName;

	@JsonProperty("Gender")
	private String genderDesc;
	
	@JsonProperty("SourceType")
	private String sourceType;

	@JsonProperty("Source")
	private String Source;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;

	@JsonProperty("WhatsappcodeDesc")
	private String     whatsappcodeDesc ;
	
	@JsonProperty("WhatsappNo")
    private String     whatsappNo ;
	
	@JsonProperty("RegionCode")
    private String     regionCode ;
}

