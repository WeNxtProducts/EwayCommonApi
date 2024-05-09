package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.admin.req.BrokerLoginInfoReq;
import com.maan.eway.admin.req.BrokerPersonalInfoReq;

import lombok.Data;

@Data
public class BrokerDatailsGetRes {

	@JsonProperty("LoginInformation")
    private BrokerLoginDetailsGetRes loginInformation      ;
	
	@JsonProperty("PersonalInformation")
    private BrokerPersonalDetailsGetRes personalInformation     ;
	
	@JsonProperty("DepositCbc")
    private List<BrokerDepositCbcDetailsGetRes> depositCbcInformation     ;
	
	@JsonProperty("BrokerLogo")
	private String brokerLogo;
}
