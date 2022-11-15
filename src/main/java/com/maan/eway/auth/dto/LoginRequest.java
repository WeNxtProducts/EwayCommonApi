package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequest {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("Password")
	private String password;
	
	@JsonProperty("ReLoginKey")
	private String reLoginKey;
	
/*	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("CompanyId")
	private String companyId; */
	


}
