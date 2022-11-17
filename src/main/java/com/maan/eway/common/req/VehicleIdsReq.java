package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VehicleIdsReq {

	@JsonProperty("Id")
	private Integer vehicleId;
	
	@JsonProperty("Covers")
	private List<CoverIdsReq> coverIdList;
}
