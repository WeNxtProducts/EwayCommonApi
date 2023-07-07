package com.maan.eway.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValidateOtp {
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("OtpToken")
	private Long otpToken;
	
	@JsonProperty("UserOTP")
	private String userOtp;
	
	@JsonProperty("CreateUser")
	private Boolean createUser;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
}
