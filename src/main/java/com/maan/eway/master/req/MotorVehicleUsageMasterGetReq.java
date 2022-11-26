package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorVehicleUsageMasterGetReq {
	
	@JsonProperty("VehicleUsageId")
    private String vehicleUsageId   ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
