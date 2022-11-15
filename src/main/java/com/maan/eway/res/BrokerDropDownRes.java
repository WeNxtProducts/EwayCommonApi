package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerDropDownRes {


	@JsonProperty("BrokerId")
	private String brokerId;
	@JsonProperty("BrokerName")
	private String brokerName;
	@JsonProperty("InsuranceId")
	private String companyId;
}
