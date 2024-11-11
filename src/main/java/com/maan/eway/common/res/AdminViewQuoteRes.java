package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class AdminViewQuoteRes {

	
	
	@JsonProperty("RiskDetails")
	private Object  riskDetails ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("RenewalOldExpDate")
	private Date renewalOldExpDate;

	@JsonProperty("RenewalDateYn")
	private String renewalDateYn;

	@JsonProperty("RenewalStatus")
	private String renewalStatus;

	@JsonProperty("RenewalOldPolicy")
	private String renewalOldPolicy;
	 
}
