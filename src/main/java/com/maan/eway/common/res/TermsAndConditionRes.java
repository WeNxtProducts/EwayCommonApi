package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;
import lombok.Data;

@Data
public class TermsAndConditionRes {


	@JsonProperty("WarrantyList")
	private List<WarrantyRes> warrantyRes;

	@JsonProperty("WarrateList")
	private List<WarrateRes> warrateRes;

	@JsonProperty("ClausesList")
	private List<ClausesRes> clausesRes;

	@JsonProperty("ExclusionList")
	private List<ExclusionRes> exclusionRes;

}
