package com.maan.eway.master.dropdown.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyBranchGetReq {

	@JsonProperty("InsuranceId")
	private String companyId ;
}
