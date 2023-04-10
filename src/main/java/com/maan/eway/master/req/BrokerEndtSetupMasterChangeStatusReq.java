package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerEndtSetupMasterChangeStatusReq {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;

	@JsonProperty("EndtSetupId")
	private String endtSetupId;

	@JsonProperty("Status")
	private String status;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("CreatedBy")
	private String createdBy;
}
