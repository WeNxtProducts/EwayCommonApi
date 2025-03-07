package com.maan.eway.claim;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PolicyInfoDetailsDto {

	@JsonProperty("PolicyNo")
	private String policyNo;
	@JsonProperty("PolicySysId")
	private String policySysId;
	@JsonProperty("PolicyEndSrNo")
	private String policyEndSrNo;

	@JsonProperty("Branch")
	private String branch;
	@JsonProperty("Broker")
	private String broker;
	@JsonProperty("Producer")
	private String producer;
	@JsonProperty("Bank")
	private String bank;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyFrom")
	private Date policyFrom;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyTo")
	private Date policyTo;
	@JsonProperty("BusinessType")
	private String businessType;
	@JsonProperty("SourceOfBusiness")
	private String sourceOfBusiness;
	@JsonProperty("FleetPolicy")
	private String fleetPolicy;
	@JsonProperty("Uwyear")
	private String uwyear;
	@JsonProperty("AgencyRepair")
	private String agencyRepair;
	@JsonProperty("ProductDesc")
	private String productDesc;
	@JsonProperty("ProductCode")
	private String productcode;
	@JsonProperty("PolicyTypeId")
	private String policytypeId;

	@JsonProperty("Endtno")
	private String endtNo;
	@JsonProperty("Customer")
	private String customer;
	@JsonProperty("Contactpername")
	private String contactPerName;
	@JsonProperty("Civilid")
	private String civilId;
	@JsonProperty("Divisioncode")
	private String divisionCode;
	@JsonProperty("Department")
	private String department;
	@JsonProperty("Insured")
	private String insured;
	@JsonProperty("Product")
	private String product;
	@JsonProperty("BrokerCode")
	private String brokerCode;
	@JsonProperty("CustomerCode")
	private String customerCode;
	@JsonProperty("OutstandingAmt")
	private String outstandingAmt;
	@JsonProperty("CurrencyCode")
	private String currencyCode;
	@JsonProperty("CurrencyName")
	private String currencyName;
	
	@JsonProperty("PolhApprSts")
	private String polhApprSts;
	
	@JsonProperty("SectionCode")
	private String sectionCode;
}
