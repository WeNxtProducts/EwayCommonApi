package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MotorPrivateCollateralDetails {

	@JsonProperty("CollateralStatus")
	private String collateralStatus;
	
	@JsonProperty("BorrowerType")
	private String borrowerType;
	
	@JsonProperty("CollateralName")
	private String collateralName;
	
	@JsonProperty("FirstLossPayee")
	private String firstLossPayee;
	
}
