package com.maan.eway.master.dropdown.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyProductDropDownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
}
