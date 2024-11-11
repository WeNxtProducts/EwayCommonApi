package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerProductGetReq {

	@JsonProperty("LoginId")
	private String loginId ;

	@JsonProperty("UserType")
	private String userType;

	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
}
