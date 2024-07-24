package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardListRes {

	@JsonProperty("PolicyList")
    private List<DasboardPolicyListRes>     policyList     ;
	@JsonProperty("QuoteList")
    private List<DasboardPolicyListRes>     quoteList     ;
	@JsonProperty("ReferalApproval")
    private List<DasboardPolicyListRes>     raList     ;
		
}
