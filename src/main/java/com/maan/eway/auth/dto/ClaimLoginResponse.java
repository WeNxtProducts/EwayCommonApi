package com.maan.eway.auth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimLoginResponse {
	
	@JsonProperty("Token")
    private String token;
	@JsonProperty("LoginId")
    private String loginId;
	
	@JsonProperty("OaCode")
    private String oaCode;
	@JsonProperty("UserName")
    private String userName;
	@JsonProperty("UserMail")
    private String userMail;
	@JsonProperty("UserMobile")
    private String userMobile;
	
	@JsonProperty("UserType")
    private String userType;
	
	@JsonProperty("SubUserType")
    private String subUserType;

	@JsonProperty("BankCode")
    private String bankCode;
	
	@JsonProperty("CountryId")
    private String countryId;
	
	@JsonProperty("CurrencyId")
    private String currencyId;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("CustomerName")
	private String customerName ;
	
	@JsonProperty("AttachedCompanies")
    private List<String>     getCompanies ;
	
	@JsonProperty("LoginBranchDetails")
    private List<LoginBranchDetailsRes> loginBranchDetails;

	@JsonProperty("BrokerCompanyProducts")
	private List<ProductDropDownRes> companyProducts;
	
	


}
