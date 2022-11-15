package com.maan.eway.notif.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SmsReq {
	
	@JsonProperty("InsuranceId")
	private String insuranceid;
	@JsonProperty("MobileNo")
	private String mobileNo;
	@JsonProperty("MobileCode")
	private String mobileCode;
	@JsonProperty("SmsContent")
	private String smsContent;
	@JsonProperty("SmsRegards")
	private String smsRegards;
	@JsonProperty("SmsSubject")
	private String smsSubject;
	@JsonProperty("CustName")
	private String custName;
	@JsonProperty("LoginId")
	private String createdBy;
	
}
