package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorVehicleUsageChangeStatusReq {

	@JsonProperty("VehicleUsageId")
	private String vehicleUsageId;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
