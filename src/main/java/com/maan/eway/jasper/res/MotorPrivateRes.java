package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorPrivateRes {

	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("EffectiveDateStart")
	private String effectiveDateStart;
	
	@JsonProperty("EffectiveDateEnd")
	private String effectiveDateEnd;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("DebitNoteNo")
	private String debitNoteNo;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("RenewalDate")
	private String renewalDate;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	@JsonProperty("InsuranceTypeDesc")
	private String insuranceTypeDesc;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("VatPremium")
	private String vatPremium;
	
	@JsonProperty("TotalPremium")
	private String totalPremium;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("ApprovedBy")
	private String approvedBy;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("NoOfVehicle")
	private String noOfVehicle;
	
	@JsonProperty("PostalAddress")
	private String postalAddress;
	
	@JsonProperty("VehicleDetails")
	private List<MotorPrivateVehicleDetails> vehicleDetails;
	
	@JsonProperty("DriverDetails")
	private List<MotorPrivateDriverDetails> driverDetails;
	
	@JsonProperty("AccessoriesDetails")
	private List<MotorPrivateAccessoriesDetails> accessoriesDetails;
	
	@JsonProperty("BorrowerType")
	private String borrowerType;
	
	@JsonProperty("CollateralName")
	private String collateralName;
	
	@JsonProperty("FirstLossPayee")
	private String firstLossPayee;
	
}
