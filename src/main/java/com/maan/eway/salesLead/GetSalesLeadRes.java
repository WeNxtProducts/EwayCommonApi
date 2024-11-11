package com.maan.eway.salesLead;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetSalesLeadRes {

	@JsonProperty("LeadId")
	private String leadId;
	
	@JsonProperty("FirstName")
	private String firstName;
	
	@JsonProperty("LastName")
	private String lastName;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("Mobile")
	private String mobile;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedDate")
	private String updatedDate;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("IntermediateId")
	private String intermediateId;
	
	@JsonProperty("IntermediateName")
	private String intermediateName;
	
	@JsonProperty("ChannelId")
	private String channelId;
	
	@JsonProperty("ChannelDesc")
	private String channelDesc;
	
	@JsonProperty("PropobabilityOfSuccessId")
	private String propobabilityOfSuccessId;
	
	@JsonProperty("PropobabilityOfSuccess")
	private String propobabilityOfSuccess;
	
	@JsonProperty("TypeOfBusinessId")
	private String typeOfBusinessId;
	
	@JsonProperty("TypeOfBusiness")
	private String typeOfBusiness;
	
	@JsonProperty("CurrentInsurer")
	private String currentInsurer;
	
}
