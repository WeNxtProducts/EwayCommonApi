package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrokerCommissionDetailsMasterGetReq {

	@JsonProperty("OaCode")
	private String oaCode;

	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("PolicyType")
	private String policyType;

	@JsonProperty("Id")
	private String id;

}

