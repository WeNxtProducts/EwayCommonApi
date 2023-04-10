package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerEndtSetupMasterGetallRes {

	@JsonProperty("EndtSetupId")
	private String endtSetupId;
	
	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;

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
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

		
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("AmendId")
	private String amendId;
	
	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("SubUserType")
	private String subUserType;

	@JsonProperty("EndtTypes")
	private List<String> endtTypes;

}
