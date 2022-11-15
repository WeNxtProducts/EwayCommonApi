package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorVehicleUsageMasterSaveReq {
	
	@JsonProperty("VehicleUsageId")
    private String vehicleUsageId   ;
	@JsonProperty("SectionId")
    private String sectionId   ;
	@JsonProperty("VehicleUsageDesc")
    private String  vehicleUsageDesc;
	@JsonProperty("Status")
    private String  status;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	@JsonProperty("Remarks")
    private String remarks;
	@JsonProperty("ClaimStatus")
    private String claimStatus;
	@JsonProperty("B2CStatus")
    private String b2cStatus;
	
}
