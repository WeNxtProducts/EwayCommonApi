package com.maan.eway.salesLead;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetSalesLeadRes {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("LeadId")
	private String leadId;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("ClientCode")
	private String clientCode;
	
	@JsonProperty("Address1")
	private String address1;
	
	@JsonProperty("Address2")
	private String address2;
	
	@JsonProperty("State")
	private String state;
	
	@JsonProperty("City")
	private String city;
	
	@JsonProperty("PinCode")
	private String pinCode;
	
	@JsonProperty("Mobile")
	private String mobile;
	
	@JsonProperty("GstIdentificationNo")
	private String gstIdentificationNo;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("LeadCreatedOn")
	private String leadCreatedOn;
	
	@JsonProperty("IntermediateId")
	private String intermediateId;
	
	@JsonProperty("IntermediateName")
	private String intermediateName;
	
	@JsonProperty("ChannelId")
	private String channelId;
	
	@JsonProperty("ChannelDesc")
	private String channelDesc;
	
	@JsonProperty("SectionTypeId")
	private String sectionTypeId;
	
	@JsonProperty("SectionTypeDesc")
	private String sectionTypeDesc;
	
	@JsonProperty("PropobabilityOfSuccessId")
	private String propobabilityOfSuccessId;
	
	@JsonProperty("PropobabilityOfSuccessDesc")
	private String propobabilityOfSuccessDesc;
	
	@JsonProperty("TypeOfBusinessId")
	private String typeOfBusinessId;
	
	@JsonProperty("TypeOfBusinessDesc")
	private String typeOfBusinessDesc;
	
	@JsonProperty("CurrentInsurer")
	private String currentInsurer;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("EntryDate")
	private String entryDate;
	
	@JsonProperty("UpdatedDate")
	private String updatedDate;
	
	@JsonProperty("EnquiryCount")
	private String enquiryCount;
	
	@JsonProperty("LeadContactPerson")
	private List<LeadContactPersonReq> leadContactPersonReq;
	
}
