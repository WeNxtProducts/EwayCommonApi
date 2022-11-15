package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.jmapper.annotations.JGlobalMap;

import lombok.Data;

@Data
public class BrokerCreationReq {

	@JsonProperty("LoginInformation")
    private BrokerLoginInfoReq loginInformation      ;
	
	@JsonProperty("PersonalInformation")
    private BrokerPersonalInfoReq personalInformation     ;
	
	
	
		

	
}
