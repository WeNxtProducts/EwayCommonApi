package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerEndtSetupMasterSaveReq {

	@JsonProperty("EndtSetupId")
	private String endtSetupId;
	
	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("OaCode")
	private String oaCode;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("SubUserType")
	private String subUserType;

	@JsonProperty("EndtTypes")
	private List<String> endtTypes;
	
}
