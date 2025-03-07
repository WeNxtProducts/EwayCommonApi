package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDetailsReq {
	
	@JsonProperty("QuotationPolicyNo")
	private String quotationPolicyNo;
	@JsonProperty("ChassisNo")
	private String chassisno;	
	@JsonProperty("EndtNo")
	private String endtNo;
	@JsonProperty("InsuranceId")
	private String insuranceId;

}
