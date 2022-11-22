package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceCustomerDetailsRes {


    @JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
    
    @JsonProperty("RequestReferenceNo")
    private String   requestReferenceNo ;
	
	
	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;

	
	@JsonProperty("IdType")
	private String idType;

	@JsonProperty("IdNumber")
	private String idNumber;

	@JsonProperty("Age")
	private String age;

	@JsonProperty("ClientName")
	private String clientName;

	@JsonProperty("TitleDesc")
	private String titleDesc;

	@JsonProperty("PolicyHolderType")
	private String policyHolderType;

	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;

	@JsonProperty("GenderDesc")
	private String genderDesc;

	@JsonProperty("OccupationDesc")
	private String occupationDesc;

	@JsonProperty("BusinessTypeDesc")
	private String businessTypeDesc;

	@JsonProperty("TelephoneNo1")
	private String telephoneNo1;
	@JsonProperty("TelephoneNo2")
	private String telephoneNo2;
	@JsonProperty("TelephoneNo3")
	private String telephoneNo3;
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	@JsonProperty("MobileNo2")
	private String mobileNo2;
	@JsonProperty("MobileNo3")
	private String mobileNo3;
	@JsonProperty("Email1")
	private String email1;
	@JsonProperty("Email2")
	private String email2;
	@JsonProperty("Email3")
	private String email3;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("UpdatedBy")
	private String updatedBy;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	

	@JsonProperty("ActualPremiumLc")
	private String actualPremiumLc;
	
	@JsonProperty("AcctualPremiumFc")
	private String actualPremiumFc ;
	
	@JsonProperty("OverallPremiumLc")
	private String overallPremiumLc ;
	
	@JsonProperty("OverallPremiumFc")
	private String    overallPremiumFc ;
	
	@JsonProperty("BrokerCode")
	private String brokerCode;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("ApplicationId")
	private String applicationId;
	
	@JsonProperty("Currency")
    private String  currency;
	
	@JsonProperty("ExchangeRate")
    private String  exchangeRate;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	  
	@JsonProperty("CustomerId")
	private String customerId;
	
	

/*	@JsonProperty("MotorList")
	private List<GetAllMotorDetailsRes> listOfEserviceMotorDetails ; */
}
