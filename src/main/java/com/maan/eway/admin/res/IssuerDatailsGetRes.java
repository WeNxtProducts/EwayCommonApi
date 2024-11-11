package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerDatailsGetRes {


	@JsonProperty("LoginInformation")
    private IssuerLoginGetRes loginInformation      ;
	
	@JsonProperty("PersonalInformation")
    private  IssuerPersonalInfoGetRes personalInformation     ;
}
