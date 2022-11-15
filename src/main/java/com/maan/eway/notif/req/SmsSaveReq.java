package com.maan.eway.notif.req;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SmsSaveReq {

	@JsonProperty("SmsRefNo")
	private String smsRefNo;
	
	@JsonProperty("CustName")
	private String custName;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("SmsType")
	private String smstype;
	
	@JsonProperty("SmsContent")
	private String smsContent;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("ReqTime")
	private Date reqTime ;
	

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("ResTime")
	private Date resTime ;
	
	@JsonProperty("ResStatus")	
	private String resStatus;
	
	@JsonProperty("ResSuccess")	
	private String resSuccess;
	
	@JsonProperty("ResCode")	
	private String resCode;
	
	@JsonProperty("ResMessage")	
	private String resMessage;
	
	@JsonProperty("Remarks")	
	private String remarks;
	
	@JsonProperty("ResData")	
	private String resData;
	
	@JsonProperty("ProductId")	
	private Integer productId;
	
	@JsonProperty("InsId")	
	private String insId;
	
	@JsonProperty("LoginId")	
	private String createdBy;
	
}
