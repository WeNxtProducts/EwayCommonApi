package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverIdsReq {


	@JsonProperty("CoverId")
	private Integer coverId;
	
	@JsonProperty("SubCoverYn")
	private String subCoverYn;

	@JsonProperty("SubCoverId")
	private String subCoverId;
}
