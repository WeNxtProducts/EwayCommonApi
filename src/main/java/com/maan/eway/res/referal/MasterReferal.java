package com.maan.eway.res.referal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class MasterReferal {
	
	@JsonProperty("isReferral")
	private Boolean isreferral;
	
	@JsonProperty("ReferralDesc")
	private String referralDesc;
	
	@JsonProperty("ApiInfo")
	private String apiInfo;

}
