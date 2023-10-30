package com.maan.eway.common.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.referal.MasterReferal;

import lombok.Data;

@Data
public class NewQuoteReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("Vehicles")
	private List<VehicleIdsReq> vehicleIdsList;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("AdminLoginId")
	private String adminLoginId;
	
	@JsonProperty("ManualReferralYn")
	private String manualReferralYn ;
	
	@JsonProperty("ReferralRemarks")
	private String referralRemarks;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("MotorYn")
	private String motorYn;
	
	
	@JsonProperty("CommissionModifyYn")
	private String commissionModifyYn;
	
	@JsonProperty("CommissionPercent")
	private String commissionPercent;

}
