package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchROPVehicleRes {
	
	@JsonProperty("RegistrationNo")
	private String resRegNumber;

	@JsonProperty("ChassisNumber")
	private String resChassisNumber;

	@JsonProperty("Make")
	private String resMake;

	@JsonProperty("Model")
	private String resModel;

	@JsonProperty("VehicleType")
	private String resBodyType;

	@JsonProperty("Color")
	private String resColor;

	@JsonProperty("YearOfManufacture")
	private Integer resYearOfManufacture;

	@JsonProperty("EngineNumber")
	private String resEngineNumber;
	
	
}

