package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CityDropDownReq {

	@JsonProperty("CountryId")
	private String countryId;
	

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("StateId")
	private String stateId;
}
