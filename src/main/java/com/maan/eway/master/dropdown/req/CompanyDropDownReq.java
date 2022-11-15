package com.maan.eway.master.dropdown.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyDropDownReq {

	@JsonProperty("BrokerCompanyYn")
	private String brokerCompanyYn ;
}
