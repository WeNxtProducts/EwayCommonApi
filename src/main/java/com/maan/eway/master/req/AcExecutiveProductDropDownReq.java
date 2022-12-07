package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveProductDropDownReq {

	@JsonProperty("BankCode")
	private String bankCode;

	@JsonProperty("CompanyId")
	private String companyId;

}
