package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class MotorMakeGetReq {

	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
