package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.jmapper.annotations.JGlobalMap;

import lombok.Data;

@Data
public class CommonLoginCreationReq {

	@JsonProperty("LoginInformation")
    private CommonLoginInformationReq     loginInformation     ;
	
	@JsonProperty("PersonalInformation")
    private CommonPersonalInforReq     personalInformation;
	
	@JsonProperty("BrokerLogo")
	private String brokerLogo;
	
	
	

}
