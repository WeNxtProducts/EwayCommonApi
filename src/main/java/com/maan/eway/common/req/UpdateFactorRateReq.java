package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class UpdateFactorRateReq {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("VehicleId")
	private Integer vehicleId;
	
	@JsonProperty("Covers")
	private List<CoverIdReq2> coverIdList;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("GroupId")
	private Integer groupId;
	
	@JsonProperty("CommissionPercentage")
	private String commissionPercentage;
	
	@JsonProperty("VatCommissison")
	private String vatCommissison;
	
	@JsonProperty("AdminLoginId")
	private String adminLoginId;
	
	@JsonProperty("LocationId")
	private String locationId;
	
}
