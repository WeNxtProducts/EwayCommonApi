package com.maan.eway.common.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class EservieMotorDetailsViewRes {

	@JsonProperty("VehicleId")
	private String vehicleId ;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("MSRefNo")
	private String msrefno;
	
	@JsonProperty("CdRefNo")
	private String cdRefNo;

	@JsonProperty("VdRefNo")
	private String vdRefNo;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	 
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	

	@JsonProperty("HavePromoCode")
    private String     havepromocode ;
	
	@JsonProperty("PromoCode")
    private String     promocode    ;
	@JsonProperty("Currency")
    private String  currency;
	
	@JsonProperty("ExchangeRate")
    private String  exchangeRate;
	
	@JsonProperty("ActualPremiumLc")
	private String actualPremiumLc;
	
	@JsonProperty("AcctualPremiumFc")
	private String actualPremiumFc ;
	
	@JsonProperty("OverallPremiumLc")
	private String overallPremiumLc ;
	
	@JsonProperty("OverallPremiumFc")
	private String    overallPremiumFc ;
		
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
    private Date policyStartDate;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
    private Date policyEndDate;
	
	@JsonProperty("CoverList")
	private List<Cover> coverList ;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	
	@JsonProperty("RejectReason")
	private String rejectReason;
	
	@JsonProperty("GroupId")
	private Integer groupId;
	
	@JsonProperty("GroupMember")
	private Integer groupMember;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("ManufactureYear")
    private Date manufactureYear;
	
	@JsonProperty("RiskDetails")
	private Object riskDetails ; 
	
	@JsonProperty("EmiYn")
	private String     emiYn;
	@JsonProperty("InstallmentPeriod")
    private String     installmentPeriod ;
	
	@JsonProperty("InstallmentMonth")
    private String     installmentMonth;
	
	@JsonProperty("DueAmount")
    private String     dueAmount;

	@JsonProperty("ReferalRemarks")
    private String     referalRemarks;
	
	
}
